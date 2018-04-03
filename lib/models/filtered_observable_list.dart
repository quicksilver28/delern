import 'dart:async';

import 'observable_list.dart';

typedef bool Filter<T>(T item);

// TODO(dotdoom): make this list read-only.
class FilteredObservableList<T> extends ObservableList<T> {
  final ObservableList<T> _base;
  StreamSubscription<ListEvent<T>> _baseEventsSubscription;
  Filter<T> _filter;

  FilteredObservableList(this._base) : super(_base.toList()) {
    _baseEventsSubscription = _base.events.listen((event) {
      switch (event.eventType) {
        case ListEventType.itemAdded:
          var item = _base[event.index];
          if (_filter == null || _filter(item)) {
            super.add(item);
          }
          break;
        case ListEventType.itemRemoved:
          var index = indexOf(event.previousValue);
          if (index >= 0) {
            super.removeAt(index);
          }
          break;
        case ListEventType.itemMoved:
          break;
        case ListEventType.itemChanged:
          var item = _base[event.index];
          if (_filter == null || _filter(item)) {
            // TODO(dotdoom): what if we have non-unique items in the list?
            // TODO(dotdoom): indexOf(previousValue) will stop working for
            //                Persistable once we implement operator== for the
            //                items. Should we use Keyed instead?
            if (indexOf(event.previousValue) == -1) {
              super.add(item);
            }
          } else {
            var index = indexOf(event.previousValue);
            if (index >= 0) {
              super.removeAt(index);
            }
          }
          break;
        case ListEventType.set:
          _refilter();
          break;
      }
    });
  }

  set filter(final Filter<T> value) {
    _filter = value;
    _refilter();
  }

  void _refilter() {
    if (_filter == null) {
      setAll(0, _base);
    } else {
      setAll(0, _base.where(_filter));
    }
  }

  @override
  void dispose() {
    _baseEventsSubscription.cancel();
    super.dispose();
  }
}