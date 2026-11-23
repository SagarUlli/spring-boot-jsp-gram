package org.jsp.jsp_gram.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.jsp.jsp_gram.dto.PostResponse;
import org.jsp.jsp_gram.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Caches each user's home feed in Redis as JSON under {@code feed:<userId>}.
 *
 * The cache is an optimisation only: if Redis is down every method degrades to
 * "cache miss" / "nothing to evict" and the feed is served from the database.
 *
 * Freshness: entries expire after a short TTL, and the actions that change a
 * feed evict the affected entries (after the DB commit). Counts shown in
 * OTHER people's feeds (likes, comments) can therefore be up to one TTL stale.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FeedCacheService {

	private static final String PREFIX = "feed:";
	private static final TypeReference<List<PostResponse>> FEED_TYPE = new TypeReference<>() {
	};

	private final StringRedisTemplate redis;
	private final ObjectMapper objectMapper;

	@Value("${app.feed-cache.ttl-seconds:60}")
	private long ttlSeconds;

	private static String key(int userId) {
		return PREFIX + userId;
	}

	public Optional<List<PostResponse>> get(int userId) {

		try {
			String json = redis.opsForValue().get(key(userId));

			if (json == null) {
				return Optional.empty();
			}

			return Optional.of(objectMapper.readValue(json, FEED_TYPE));

		} catch (Exception e) {
			log.warn("Feed cache read failed for user {}: {}", userId, e.toString());
			return Optional.empty();
		}
	}

	public void put(int userId, List<PostResponse> feed) {

		try {
			redis.opsForValue().set(key(userId), objectMapper.writeValueAsString(feed), Duration.ofSeconds(ttlSeconds));
		} catch (Exception e) {
			log.warn("Feed cache write failed for user {}: {}", userId, e.toString());
		}
	}

	/** Evicts one user's feed once the current transaction has committed. */
	public void evict(int userId) {
		evictAll(List.of(userId));
	}

	/** Evicts the author's feed and the feeds of everyone who follows them. */
	public void evictAuthorAndFollowers(User author) {

		List<Integer> ids = new ArrayList<>();
		ids.add(author.getId());
		author.getFollowers().forEach(follower -> ids.add(follower.getId()));

		evictAll(ids);
	}

	public void evictAll(Collection<Integer> userIds) {

		List<String> keys = userIds.stream().map(FeedCacheService::key).toList();

		// Evicting before commit would let a concurrent request re-cache stale data,
		// so wait for the commit when we are inside a transaction.
		if (TransactionSynchronizationManager.isSynchronizationActive()) {
			TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
				@Override
				public void afterCommit() {
					delete(keys);
				}
			});
		} else {
			delete(keys);
		}
	}

	private void delete(List<String> keys) {
		try {
			redis.delete(keys);
		} catch (Exception e) {
			log.warn("Feed cache eviction failed: {}", e.toString());
		}
	}
}
