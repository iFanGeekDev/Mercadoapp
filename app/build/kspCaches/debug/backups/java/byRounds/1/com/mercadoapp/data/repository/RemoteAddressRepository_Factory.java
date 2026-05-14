package com.mercadoapp.data.repository;

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
public final class RemoteAddressRepository_Factory implements Factory<RemoteAddressRepository> {
  private final Provider<MercadoApiService> apiProvider;

  public RemoteAddressRepository_Factory(Provider<MercadoApiService> apiProvider) {
    this.apiProvider = apiProvider;
  }

  @Override
  public RemoteAddressRepository get() {
    return newInstance(apiProvider.get());
  }

  public static RemoteAddressRepository_Factory create(Provider<MercadoApiService> apiProvider) {
    return new RemoteAddressRepository_Factory(apiProvider);
  }

  public static RemoteAddressRepository newInstance(MercadoApiService api) {
    return new RemoteAddressRepository(api);
  }
}
