package com.mercadoapp.data.repository;

import com.mercadoapp.data.remote.api.MercadoApiService;
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

  public UserRepositoryImpl_Factory(Provider<MercadoApiService> apiServiceProvider) {
    this.apiServiceProvider = apiServiceProvider;
  }

  @Override
  public UserRepositoryImpl get() {
    return newInstance(apiServiceProvider.get());
  }

  public static UserRepositoryImpl_Factory create(Provider<MercadoApiService> apiServiceProvider) {
    return new UserRepositoryImpl_Factory(apiServiceProvider);
  }

  public static UserRepositoryImpl newInstance(MercadoApiService apiService) {
    return new UserRepositoryImpl(apiService);
  }
}
