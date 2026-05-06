package com.mercadoapp.data.repository;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class FakeProductRepository_Factory implements Factory<FakeProductRepository> {
  @Override
  public FakeProductRepository get() {
    return newInstance();
  }

  public static FakeProductRepository_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static FakeProductRepository newInstance() {
    return new FakeProductRepository();
  }

  private static final class InstanceHolder {
    private static final FakeProductRepository_Factory INSTANCE = new FakeProductRepository_Factory();
  }
}
