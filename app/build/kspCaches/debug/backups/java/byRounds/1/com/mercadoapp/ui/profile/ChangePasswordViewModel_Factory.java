package com.mercadoapp.ui.profile;

import com.mercadoapp.domain.repository.UserRepository;
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
public final class ChangePasswordViewModel_Factory implements Factory<ChangePasswordViewModel> {
  private final Provider<UserRepository> userRepositoryProvider;

  public ChangePasswordViewModel_Factory(Provider<UserRepository> userRepositoryProvider) {
    this.userRepositoryProvider = userRepositoryProvider;
  }

  @Override
  public ChangePasswordViewModel get() {
    return newInstance(userRepositoryProvider.get());
  }

  public static ChangePasswordViewModel_Factory create(
      Provider<UserRepository> userRepositoryProvider) {
    return new ChangePasswordViewModel_Factory(userRepositoryProvider);
  }

  public static ChangePasswordViewModel newInstance(UserRepository userRepository) {
    return new ChangePasswordViewModel(userRepository);
  }
}
