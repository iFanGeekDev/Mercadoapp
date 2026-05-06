package com.mercadoapp.data.remote.interceptor;

import com.mercadoapp.data.local.datastore.AuthDataStore;
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
public final class AuthInterceptor_Factory implements Factory<AuthInterceptor> {
  private final Provider<AuthDataStore> authDataStoreProvider;

  public AuthInterceptor_Factory(Provider<AuthDataStore> authDataStoreProvider) {
    this.authDataStoreProvider = authDataStoreProvider;
  }

  @Override
  public AuthInterceptor get() {
    return newInstance(authDataStoreProvider.get());
  }

  public static AuthInterceptor_Factory create(Provider<AuthDataStore> authDataStoreProvider) {
    return new AuthInterceptor_Factory(authDataStoreProvider);
  }

  public static AuthInterceptor newInstance(AuthDataStore authDataStore) {
    return new AuthInterceptor(authDataStore);
  }
}
