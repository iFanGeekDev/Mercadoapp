package com.mercadoapp.data.repository;

import com.mercadoapp.data.remote.api.MercadoApiService;
import com.mercadoapp.domain.repository.AuthRepository;
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
public final class UserRepositoryImpl_Factory implements Factory<UserRepositoryImpl> {
  private final Provider<MercadoApiService> apiServiceProvider;

  private final Provider<AuthRepository> authRepositoryProvider;

  public UserRepositoryImpl_Factory(Provider<MercadoApiService> apiServiceProvider,
      Provider<AuthRepository> authRepositoryProvider) {
    this.apiServiceProvider = apiServiceProvider;
    this.authRepositoryProvider = authRepositoryProvider;
  }

  @Override
  public UserRepositoryImpl get() {
    return newInstance(apiServiceProvider.get(), authRepositoryProvider.get());
  }

  public static UserRepositoryImpl_Factory create(Provider<MercadoApiService> apiServiceProvider,
      Provider<AuthRepository> authRepositoryProvider) {
    return new UserRepositoryImpl_Factory(apiServiceProvider, authRepositoryProvider);
  }

  public static UserRepositoryImpl newInstance(MercadoApiService apiService,
      AuthRepository authRepository) {
    return new UserRepositoryImpl(apiService, authRepository);
  }
}
