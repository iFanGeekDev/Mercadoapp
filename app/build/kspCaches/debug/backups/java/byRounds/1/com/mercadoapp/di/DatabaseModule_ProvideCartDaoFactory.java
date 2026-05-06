package com.mercadoapp.di;

import com.mercadoapp.data.local.dao.CartDao;
import com.mercadoapp.data.local.db.MercadoDatabase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class DatabaseModule_ProvideCartDaoFactory implements Factory<CartDao> {
  private final Provider<MercadoDatabase> dbProvider;

  public DatabaseModule_ProvideCartDaoFactory(Provider<MercadoDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public CartDao get() {
    return provideCartDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideCartDaoFactory create(Provider<MercadoDatabase> dbProvider) {
    return new DatabaseModule_ProvideCartDaoFactory(dbProvider);
  }

  public static CartDao provideCartDao(MercadoDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideCartDao(db));
  }
}
