package com.mercadoapp.ui.product_list;

import androidx.lifecycle.SavedStateHandle;
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
public final class ProductListViewModel_Factory implements Factory<ProductListViewModel> {
  private final Provider<ProductRepository> repositoryProvider;

  private final Provider<SavedStateHandle> savedStateHandleProvider;

  public ProductListViewModel_Factory(Provider<ProductRepository> repositoryProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    this.repositoryProvider = repositoryProvider;
    this.savedStateHandleProvider = savedStateHandleProvider;
  }

  @Override
  public ProductListViewModel get() {
    return newInstance(repositoryProvider.get(), savedStateHandleProvider.get());
  }

  public static ProductListViewModel_Factory create(Provider<ProductRepository> repositoryProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    return new ProductListViewModel_Factory(repositoryProvider, savedStateHandleProvider);
  }

  public static ProductListViewModel newInstance(ProductRepository repository,
      SavedStateHandle savedStateHandle) {
    return new ProductListViewModel(repository, savedStateHandle);
  }
}
