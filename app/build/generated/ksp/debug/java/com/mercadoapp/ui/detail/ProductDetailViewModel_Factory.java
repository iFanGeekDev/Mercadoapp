package com.mercadoapp.ui.detail;

import androidx.lifecycle.SavedStateHandle;
import com.mercadoapp.domain.repository.CartRepository;
import com.mercadoapp.domain.repository.ProductRepository;
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
public final class ProductDetailViewModel_Factory implements Factory<ProductDetailViewModel> {
  private final Provider<SavedStateHandle> savedStateHandleProvider;

  private final Provider<ProductRepository> repositoryProvider;

  private final Provider<CartRepository> cartRepositoryProvider;

  public ProductDetailViewModel_Factory(Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<ProductRepository> repositoryProvider,
      Provider<CartRepository> cartRepositoryProvider) {
    this.savedStateHandleProvider = savedStateHandleProvider;
    this.repositoryProvider = repositoryProvider;
    this.cartRepositoryProvider = cartRepositoryProvider;
  }

  @Override
  public ProductDetailViewModel get() {
    return newInstance(savedStateHandleProvider.get(), repositoryProvider.get(), cartRepositoryProvider.get());
  }

  public static ProductDetailViewModel_Factory create(
      Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<ProductRepository> repositoryProvider,
      Provider<CartRepository> cartRepositoryProvider) {
    return new ProductDetailViewModel_Factory(savedStateHandleProvider, repositoryProvider, cartRepositoryProvider);
  }

  public static ProductDetailViewModel newInstance(SavedStateHandle savedStateHandle,
      ProductRepository repository, CartRepository cartRepository) {
    return new ProductDetailViewModel(savedStateHandle, repository, cartRepository);
  }
}
