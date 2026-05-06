package com.mercadoapp.di;

import com.mercadoapp.data.remote.api.MercadoApiService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import retrofit2.Retrofit;

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
public final class NetworkModule_ProvideMercadoApiServiceFactory implements Factory<MercadoApiService> {
  private final Provider<Retrofit> retrofitProvider;

  public NetworkModule_ProvideMercadoApiServiceFactory(Provider<Retrofit> retrofitProvider) {
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public MercadoApiService get() {
    return provideMercadoApiService(retrofitProvider.get());
  }

  public static NetworkModule_ProvideMercadoApiServiceFactory create(
      Provider<Retrofit> retrofitProvider) {
    return new NetworkModule_ProvideMercadoApiServiceFactory(retrofitProvider);
  }

  public static MercadoApiService provideMercadoApiService(Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideMercadoApiService(retrofit));
  }
}
