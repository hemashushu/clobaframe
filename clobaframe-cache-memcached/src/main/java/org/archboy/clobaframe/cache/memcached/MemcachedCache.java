package org.archboy.clobaframe.cache.memcached;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Named;
import net.spy.memcached.AddrUtil;
import net.spy.memcached.ConnectionFactoryBuilder;
import net.spy.memcached.ConnectionFactoryBuilder.Protocol;
import net.spy.memcached.MemcachedClient;
import org.archboy.clobaframe.cache.impl.AbstractCache;
import org.archboy.clobaframe.cache.Cache;
import org.archboy.clobaframe.cache.Expiration;
import org.springframework.beans.factory.annotation.Value;

/**
 * Memcached implementation.
 *
 * @author yang
 *
 */
@Named
public class MemcachedCache extends AbstractCache implements Closeable {

	private MemcachedClient client;

	private static final Protocol DEFAULT_PROTOCOL = Protocol.TEXT;
	private static final String DEFAULT_SERVERS = "127.0.0.1:11211";
	private static final String DEFAULT_SPY_MEMCACHED_LOGGER = "net.spy.memcached.compat.log.Log4JLogger";

	private String spymemcachedLogger = DEFAULT_SPY_MEMCACHED_LOGGER;

	@Value("${clobaframe.cache.memcached.protocol}")
	private Protocol protocol = DEFAULT_PROTOCOL;

	@Value("${clobaframe.cache.memcached.servers}")
	private String servers = DEFAULT_SERVERS;

	@PostConstruct
	public void init() throws IOException{

		// set spymemcached logger
		System.setProperty("net.spy.log.LoggerImpl", spymemcachedLogger);

		ConnectionFactoryBuilder builder = new ConnectionFactoryBuilder();
		builder.setProtocol(protocol);
		client = new MemcachedClient(builder.build(), AddrUtil.getAddresses(servers));
	}

	@PreDestroy
	@Override
	public void close(){
		client.shutdown();
	}

	public void setSpymemcachedLogger(String spymemcachedLogger) {
		this.spymemcachedLogger = spymemcachedLogger;
	}

	@Override
	public String getName() {
		return "memcached";
	}

	@Override
	public void clearAll() {
		client.flush();
	}

	@Override
	public boolean delete(String key) {
		Future<Boolean> result = client.delete(key);

		try{
			return result.get().booleanValue();
		}catch(ExecutionException e){
			// ignore
		}catch(InterruptedException e){
			// ignore
		}

		return false;
	}

	@Override
	public void deleteAll(Collection<String> keys) {
		for (String key : keys) {
			delete(key);
		}
	}

	@Override
	public Object get(String key) {
		return client.get(key);
	}

	@Override
	public Map<String, Object> getAll(Collection<String> keys) {
		return client.getBulk(keys);
	}

	@Override
	public boolean put(String key, Object value, Expiration expires,
			Policy policy) {
		int expireSecond = 0;
		if (expires != null) {
			expireSecond = expires.getSeconds();
		}

		Future<Boolean> result = null;
		switch (policy) {
			case SET_ALWAYS:
				result = client.set(key, expireSecond, value);
				break;
			case ADD_ONLY_IF_NOT_PRESENT:
				result = client.add(key, expireSecond, value);
				break;
			case REPLACE_ONLY_IF_PRESENT:
				result = client.replace(key, expireSecond, value);
				break;
		}

		try{
			return result.get().booleanValue();
		}catch(ExecutionException e){
			// ignore
		}catch(InterruptedException e){
			// ignore
		}

		return false;
	}

	@Override
	public Set<String> putAll(Map<String, ? extends Object> values,
			Expiration expires, Policy policy) {
		Set<String> items = new HashSet<String>();
		for (String key : values.keySet()) {
			boolean created = put(key, values.get(key), expires, policy);
			if (created) {
				items.add(key);
			}
		}
		return items;
	}

	@Override
	public boolean put(String key, Object value) {
		return put(key, value, null, Policy.SET_ALWAYS);
	}

	@Override
	public boolean put(String key, Object value, Expiration expiration) {
		return put(key, value, expiration, Cache.Policy.SET_ALWAYS);
	}
}
