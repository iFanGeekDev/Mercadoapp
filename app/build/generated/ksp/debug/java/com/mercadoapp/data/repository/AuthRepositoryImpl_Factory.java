package com.mercadoapp.data.repository;

import com.mercadoapp.data.local.datastore.AuthDataStore;
import com.mercadoapp.data.remote.api.MercadoApiService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class AuthRepositoryImpl_Factory implements Factory<AuthRepositoryImpl> {
  private final Provider<MercadoApiService> apiProvider;

  private final Provider<AuthDataStore> authDataStoreProvider;

  public AuthRepositoryImpl_Factory(Provider<MercadoApiService> apiProvider,
      Provider<AuthDataStore> authDataStoreProvider) {
    this.apiProvider = apiProvider;
    this.authDataStoreProvider = authDataStoreProvider;
  }

  @Override
  public AuthRepositoryImpl get() {
    return newInstance(apiProvider.get(), authDataStoreProvider.get());
  }

  public static AuthRepositoryImpl_Factory create(Provider<MercadoApiService> apiProvider,
      Provider<AuthDataStore> authDataStoreProvider) {
    return new AuthRepositoryImpl_Factory(apiProvider, authDataStoreProvider);
  }

  public static AuthRepositoryImpl newInstance(MercadoApiService api, AuthDataStore authDataStore) {
    return new AuthRepositoryImpl(api, authDataStore);
  }
}
