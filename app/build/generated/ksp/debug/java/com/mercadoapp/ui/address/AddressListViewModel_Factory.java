package com.mercadoapp.ui.address;

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
public final class AddressListViewModel_Factory implements Factory<AddressListViewModel> {
  private final Provider<AddressRepository> addressRepositoryProvider;

  public AddressListViewModel_Factory(Provider<AddressRepository> addressRepositoryProvider) {
    this.addressRepositoryProvider = addressRepositoryProvider;
  }

  @Override
  public AddressListViewModel get() {
    return newInstance(addressRepositoryProvider.get());
  }

  public static AddressListViewModel_Factory create(
      Provider<AddressRepository> addressRepositoryProvider) {
    return new AddressListViewModel_Factory(addressRepositoryProvider);
  }

  public static AddressListViewModel newInstance(AddressRepository addressRepository) {
    return new AddressListViewModel(addressRepository);
  }
}
