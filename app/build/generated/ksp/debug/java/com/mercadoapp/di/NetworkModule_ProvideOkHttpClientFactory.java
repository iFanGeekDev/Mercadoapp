package com.mercadoapp.di;

import android.content.Context;
import com.mercadoapp.data.remote.interceptor.AuthInterceptor;
import com.mercadoapp.data.remote.interceptor.TokenRefreshInterceptor;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import okhttp3.OkHttpClient;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class NetworkModule_ProvideOkHttpClientFactory implements Factory<OkHttpClient> {
  private final Provider<Context> contextProvider;

  private final Provider<AuthInterceptor> authInterceptorProvider;

  private final Provider<TokenRefreshInterceptor> tokenRefreshInterceptorProvider;

  public NetworkModule_ProvideOkHttpClientFactory(Provider<Context> contextProvider,
      Provider<AuthInterceptor> authInterceptorProvider,
      Provider<TokenRefreshInterceptor> tokenRefreshInterceptorProvider) {
    this.contextProvider = contextProvider;
    this.authInterceptorProvider = authInterceptorProvider;
    this.tokenRefreshInterceptorProvider = tokenRefreshInterceptorProvider;
  }

  @Override
  public OkHttpClient get() {
    return provideOkHttpClient(contextProvider.get(), authInterceptorProvider.get(), tokenRefreshInterceptorProvider.get());
  }

  public static NetworkModule_ProvideOkHttpClientFactory create(Provider<Context> contextProvider,
      Provider<AuthInterceptor> authInterceptorProvider,
      Provider<TokenRefreshInterceptor> tokenRefreshInterceptorProvider) {
    return new NetworkModule_ProvideOkHttpClientFactory(contextProvider, authInterceptorProvider, tokenRefreshInterceptorProvider);
  }

  public static OkHttpClient provideOkHttpClient(Context context, AuthInterceptor authInterceptor,
      TokenRefreshInterceptor tokenRefreshInterceptor) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideOkHttpClient(context, authInterceptor, tokenRefreshInterceptor));
  }
}
