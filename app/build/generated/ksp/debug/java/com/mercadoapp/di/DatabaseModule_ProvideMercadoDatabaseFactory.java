package com.mercadoapp.di;

import android.content.Context;
import com.mercadoapp.data.local.db.MercadoDatabase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class DatabaseModule_ProvideMercadoDatabaseFactory implements Factory<MercadoDatabase> {
  private final Provider<Context> contextProvider;

  public DatabaseModule_ProvideMercadoDatabaseFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public MercadoDatabase get() {
    return provideMercadoDatabase(contextProvider.get());
  }

  public static DatabaseModule_ProvideMercadoDatabaseFactory create(
      Provider<Context> contextProvider) {
    return new DatabaseModule_ProvideMercadoDatabaseFactory(contextProvider);
  }

  public static MercadoDatabase provideMercadoDatabase(Context context) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideMercadoDatabase(context));
  }
}
