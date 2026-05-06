package com.mercadoapp.data.repository;

import com.mercadoapp.data.local.dao.ProductDao;
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
public final class RemoteProductRepository_Factory implements Factory<RemoteProductRepository> {
  private final Provider<MercadoApiService> apiProvider;

  private final Provider<ProductDao> productDaoProvider;

  public RemoteProductRepository_Factory(Provider<MercadoApiService> apiProvider,
      Provider<ProductDao> productDaoProvider) {
    this.apiProvider = apiProvider;
    this.productDaoProvider = productDaoProvider;
  }

  @Override
  public RemoteProductRepository get() {
    return newInstance(apiProvider.get(), productDaoProvider.get());
  }

  public static RemoteProductRepository_Factory create(Provider<MercadoApiService> apiProvider,
      Provider<ProductDao> productDaoProvider) {
    return new RemoteProductRepository_Factory(apiProvider, productDaoProvider);
  }

  public static RemoteProductRepository newInstance(MercadoApiService api, ProductDao productDao) {
    return new RemoteProductRepository(api, productDao);
  }
}
