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
public final class TokenRefreshInterceptor_Factory implements Factory<TokenRefreshInterceptor> {
  private final Provider<AuthDataStore> authDataStoreProvider;

  public TokenRefreshInterceptor_Factory(Provider<AuthDataStore> authDataStoreProvider) {
    this.authDataStoreProvider = authDataStoreProvider;
  }

  @Override
  public TokenRefreshInterceptor get() {
    return newInstance(authDataStoreProvider.get());
  }

  public static TokenRefreshInterceptor_Factory create(
      Provider<AuthDataStore> authDataStoreProvider) {
    return new TokenRefreshInterceptor_Factory(authDataStoreProvider);
  }

  public static TokenRefreshInterceptor newInstance(AuthDataStore authDataStore) {
    return new TokenRefreshInterceptor(authDataStore);
  }
}
