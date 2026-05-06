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
public final class EditProfileViewModel_Factory implements Factory<EditProfileViewModel> {
  private final Provider<UserRepository> userRepositoryProvider;

  public EditProfileViewModel_Factory(Provider<UserRepository> userRepositoryProvider) {
    this.userRepositoryProvider = userRepositoryProvider;
  }

  @Override
  public EditProfileViewModel get() {
    return newInstance(userRepositoryProvider.get());
  }

  public static EditProfileViewModel_Factory create(
      Provider<UserRepository> userRepositoryProvider) {
    return new EditProfileViewModel_Factory(userRepositoryProvider);
  }

  public static EditProfileViewModel newInstance(UserRepository userRepository) {
    return new EditProfileViewModel(userRepository);
  }
}
