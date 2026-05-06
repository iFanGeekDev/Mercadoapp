package com.mercadoapp.ui.checkout;

import com.mercadoapp.domain.repository.AddressRepository;
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
public final class CheckoutViewModel_Factory implements Factory<CheckoutViewModel> {
  private final Provider<AddressRepository> addressRepositoryProvider;

  public CheckoutViewModel_Factory(Provider<AddressRepository> addressRepositoryProvider) {
    this.addressRepositoryProvider = addressRepositoryProvider;
  }

  @Override
  public CheckoutViewModel get() {
    return newInstance(addressRepositoryProvider.get());
  }

  public static CheckoutViewModel_Factory create(
      Provider<AddressRepository> addressRepositoryProvider) {
    return new CheckoutViewModel_Factory(addressRepositoryProvider);
  }

  public static CheckoutViewModel newInstance(AddressRepository addressRepository) {
    return new CheckoutViewModel(addressRepository);
  }
}
