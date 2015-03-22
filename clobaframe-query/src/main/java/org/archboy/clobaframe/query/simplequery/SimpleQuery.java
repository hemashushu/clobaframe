package org.archboy.clobaframe.query.simplequery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.archboy.clobaframe.query.DefaultViewModel;
import org.archboy.clobaframe.query.Query;
import org.archboy.clobaframe.query.ViewModel;
import org.springframework.util.Assert;

/**
 *
 * @author yang
 * @param <T>
 */
public class SimpleQuery<T> implements Query<T>{

	private Collection<T> collection;

	private List<Predicate> predicates = null;
	private List<Comparator<T>> comparators = null;

	private int limit;
	
	public SimpleQuery(Collection<T> collection) {
		this.collection = collection;
	}

	public static <T> Query<T> from(Collection<T> collection){
		Assert.notNull(collection);
		
		return new SimpleQuery<T>(collection);
	}

	@Override
	public Query<T> where(Predicate predicate) {
		Assert.notNull(predicate);
		
		if (predicates == null){
			predicates = new ArrayList<Predicate>();
		}
		predicates.add(predicate);
		return this;
	}

	@Override
	public Query<T> whereEquals(String key, Object value) {
		Assert.hasText(key);
		
		Predicate predicate = PredicateFactory.equals(key, value);
		return where(predicate);
	}

	@Override
	public Query<T> whereNotEquals(String key, Object value) {
		Assert.hasText(key);
		
		Predicate predicateEquals = PredicateFactory.equals(key, value);
		Predicate predicate = PredicateFactory.not(predicateEquals);
		return where(predicate);
	}

	@Override
	public Query<T> whereGreaterThan(String key, Object value) {
		Assert.hasText(key);
		
		Predicate predicate = PredicateFactory.greaterThan(key, value);
		return where(predicate);
	}

	@Override
	public Query<T> whereGreaterThanOrEqual(String key, Object value) {
		Assert.hasText(key);
		
		Predicate predicateLessThan = PredicateFactory.lessThan(key, value);
		Predicate predicate = PredicateFactory.not(predicateLessThan);
		return where(predicate);
	}

	@Override
	public Query<T> whereLessThan(String key, Object value) {
		Assert.hasText(key);
		
		Predicate predicate = PredicateFactory.lessThan(key, value);
		return where(predicate);
	}

	@Override
	public Query<T> whereLessThanOrEqual(String key, Object value) {
		Assert.hasText(key);
		
		Predicate predicateGreaterThan = PredicateFactory.greaterThan(key, value);
		Predicate predicate = PredicateFactory.not(predicateGreaterThan);
		return where(predicate);
	}

	@Override
	public Query<T> orderBy(String key) {
		Assert.hasText(key);
		
		Comparator<T> comparator = ComparatorFactory.build(key, true);
		return orderBy(comparator);
	}

	@Override
	public Query<T> orderByDesc(String key) {
		Assert.hasText(key);
		
		Comparator<T> comparator = ComparatorFactory.build(key, false);
		return orderBy(comparator);
	}

	@Override
	public Query<T> orderBy(Comparator<T> comparator) {
		Assert.notNull(comparator);
		
		if (comparators == null){
			comparators = new ArrayList<Comparator<T>>();
		}
		comparators.add(comparator);

		return this;
	}

	@Override
	public Query<T> limit(int size) {
		Assert.isTrue(size >0);
		this.limit = size;
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> list() {
		Collection<T> items = collection;

		if (predicates != null) {
			Predicate[] predicateArray = predicates.toArray(new Predicate[0]);
			Predicate predicate = PredicateFactory.and(predicateArray);
			items = (Collection<T>) CollectionUtils.select(items, predicate);
		}

		if (items.isEmpty() || comparators == null) {
			if (limit == 0) { // un-specify the limit size
				return new ArrayList<T>(items);
			} else {
				return copy(items, limit);
			}
		}

		Comparator<T>[] comparatorArray = (Comparator<T>[]) comparators.toArray(new Comparator[0]);
		Comparator<T> comparator = ComparatorFactory.combine(comparatorArray);

		List<T> result = new ArrayList<T>(items);
		Collections.sort(result, comparator);

		if (limit == 0) { // un-specify the limit size
			return result;
		} else {
			return copy(result, limit);
		}
	}

	private List<T> copy(Collection<T> items, int maxItems){
		int count = (maxItems > items.size() ? items.size() : maxItems);
		
		List<T> result = new ArrayList<T>(count);
		int done = 0;
		for(T t : items) {
			done++;
			if (done > count) {
				break;
			}
			result.add(t);
		}
		return result;
	}
	
	@Override
	public T first() {
		Collection<T> items = list();
		if (items.isEmpty()){
			return null;
		}else{
			return items.iterator().next();
		}
	}

	@Override
	public List<ViewModel> select(String... keys) {
		Assert.notNull(keys);
		
		List<ViewModel> result = new ArrayList<ViewModel>();
		Collection<T> items = list();

		Set<String> names = new TreeSet<String>();
		Collections.addAll(names, keys);
		
		for (T item : items) {
			ViewModel viewModel = DefaultViewModel.Wrap(item, names);
			result.add(viewModel);
		}

		return result;
	}
}
