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
public final class RemoteOrderRepository_Factory implements Factory<RemoteOrderRepository> {
  private final Provider<MercadoApiService> apiProvider;

  public RemoteOrderRepository_Factory(Provider<MercadoApiService> apiProvider) {
    this.apiProvider = apiProvider;
  }

  @Override
  public RemoteOrderRepository get() {
    return newInstance(apiProvider.get());
  }

  public static RemoteOrderRepository_Factory create(Provider<MercadoApiService> apiProvider) {
    return new RemoteOrderRepository_Factory(apiProvider);
  }

  public static RemoteOrderRepository newInstance(MercadoApiService api) {
    return new RemoteOrderRepository(api);
  }
}
