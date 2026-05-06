package com.mercadoapp.ui.order;

import com.mercadoapp.domain.repository.OrderRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation"
})
public final class OrderHistoryViewModel_Factory implements Factory<OrderHistoryViewModel> {
  private final Provider<OrderRepository> orderRepositoryProvider;

  public OrderHistoryViewModel_Factory(Provider<OrderRepository> orderRepositoryProvider) {
    this.orderRepositoryProvider = orderRepositoryProvider;
  }

  @Override
  public OrderHistoryViewModel get() {
    return newInstance(orderRepositoryProvider.get());
  }

  public static OrderHistoryViewModel_Factory create(
      Provider<OrderRepository> orderRepositoryProvider) {
    return new OrderHistoryViewModel_Factory(orderRepositoryProvider);
  }

  public static OrderHistoryViewModel newInstance(OrderRepository orderRepository) {
    return new OrderHistoryViewModel(orderRepository);
  }
}
