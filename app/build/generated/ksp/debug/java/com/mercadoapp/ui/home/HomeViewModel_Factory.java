package com.mercadoapp.ui.home;

import com.mercadoapp.domain.repository.AuthRepository;
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
public final class HomeViewModel_Factory implements Factory<HomeViewModel> {
  private final Provider<ProductRepository> repositoryProvider;

  private final Provider<AuthRepository> authRepositoryProvider;

  public HomeViewModel_Factory(Provider<ProductRepository> repositoryProvider,
      Provider<AuthRepository> authRepositoryProvider) {
    this.repositoryProvider = repositoryProvider;
    this.authRepositoryProvider = authRepositoryProvider;
  }

  @Override
  public HomeViewModel get() {
    return newInstance(repositoryProvider.get(), authRepositoryProvider.get());
  }

  public static HomeViewModel_Factory create(Provider<ProductRepository> repositoryProvider,
      Provider<AuthRepository> authRepositoryProvider) {
    return new HomeViewModel_Factory(repositoryProvider, authRepositoryProvider);
  }

  public static HomeViewModel newInstance(ProductRepository repository,
      AuthRepository authRepository) {
    return new HomeViewModel(repository, authRepository);
  }
}
