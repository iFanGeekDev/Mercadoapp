package com.mercadoapp.data.repository;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class FakeAddressRepository_Factory implements Factory<FakeAddressRepository> {
  @Override
  public FakeAddressRepository get() {
    return newInstance();
  }

  public static FakeAddressRepository_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static FakeAddressRepository newInstance() {
    return new FakeAddressRepository();
  }

  private static final class InstanceHolder {
    private static final FakeAddressRepository_Factory INSTANCE = new FakeAddressRepository_Factory();
  }
}
