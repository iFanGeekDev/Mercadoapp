package com.mercadoapp.ui.address;

import androidx.lifecycle.SavedStateHandle;
import com.mercadoapp.data.remote.api.MercadoApiService;
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
public final class AddressEditViewModel_Factory implements Factory<AddressEditViewModel> {
  private final Provider<AddressRepository> addressRepositoryProvider;

  private final Provider<MercadoApiService> apiServiceProvider;

  private final Provider<SavedStateHandle> savedStateHandleProvider;

  public AddressEditViewModel_Factory(Provider<AddressRepository> addressRepositoryProvider,
      Provider<MercadoApiService> apiServiceProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    this.addressRepositoryProvider = addressRepositoryProvider;
    this.apiServiceProvider = apiServiceProvider;
    this.savedStateHandleProvider = savedStateHandleProvider;
  }

  @Override
  public AddressEditViewModel get() {
    return newInstance(addressRepositoryProvider.get(), apiServiceProvider.get(), savedStateHandleProvider.get());
  }

  public static AddressEditViewModel_Factory create(
      Provider<AddressRepository> addressRepositoryProvider,
      Provider<MercadoApiService> apiServiceProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    return new AddressEditViewModel_Factory(addressRepositoryProvider, apiServiceProvider, savedStateHandleProvider);
  }

  public static AddressEditViewModel newInstance(AddressRepository addressRepository,
      MercadoApiService apiService, SavedStateHandle savedStateHandle) {
    return new AddressEditViewModel(addressRepository, apiService, savedStateHandle);
  }
}
