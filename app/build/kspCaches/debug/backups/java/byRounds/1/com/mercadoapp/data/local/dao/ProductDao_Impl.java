package com.mercadoapp.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.mercadoapp.data.local.entity.ProductEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ProductDao_Impl implements ProductDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ProductEntity> __insertionAdapterOfProductEntity;

  private final SharedSQLiteStatement __preparedStmtOfClearAll;

  public ProductDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfProductEntity = new EntityInsertionAdapter<ProductEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `products` (`id`,`name`,`imageUrl`,`shortDescription`,`technicalSpecsJson`,`variantsJson`,`isOffer`,`isNewArrival`,`updatedAt`) VALUES (?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ProductEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getImageUrl());
        statement.bindString(4, entity.getShortDescription());
        statement.bindString(5, entity.getTechnicalSpecsJson());
        statement.bindString(6, entity.getVariantsJson());
        final int _tmp = entity.isOffer() ? 1 : 0;
        statement.bindLong(7, _tmp);
        final int _tmp_1 = entity.isNewArrival() ? 1 : 0;
        statement.bindLong(8, _tmp_1);
        statement.bindLong(9, entity.getUpdatedAt());
      }
    };
    this.__preparedStmtOfClearAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM products";
        return _query;
      }
    };
  }

  @Override
  public Object upsertAll(final List<ProductEntity> products,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfProductEntity.insert(products);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object clearAll(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearAll.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClearAll.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<ProductEntity>> observeAll() {
    final String _sql = "SELECT * FROM products ORDER BY updatedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"products"}, new Callable<List<ProductEntity>>() {
      @Override
      @NonNull
      public List<ProductEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfImageUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "imageUrl");
          final int _cursorIndexOfShortDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "shortDescription");
          final int _cursorIndexOfTechnicalSpecsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "technicalSpecsJson");
          final int _cursorIndexOfVariantsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "variantsJson");
          final int _cursorIndexOfIsOffer = CursorUtil.getColumnIndexOrThrow(_cursor, "isOffer");
          final int _cursorIndexOfIsNewArrival = CursorUtil.getColumnIndexOrThrow(_cursor, "isNewArrival");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<ProductEntity> _result = new ArrayList<ProductEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ProductEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpImageUrl;
            _tmpImageUrl = _cursor.getString(_cursorIndexOfImageUrl);
            final String _tmpShortDescription;
            _tmpShortDescription = _cursor.getString(_cursorIndexOfShortDescription);
            final String _tmpTechnicalSpecsJson;
            _tmpTechnicalSpecsJson = _cursor.getString(_cursorIndexOfTechnicalSpecsJson);
            final String _tmpVariantsJson;
            _tmpVariantsJson = _cursor.getString(_cursorIndexOfVariantsJson);
            final boolean _tmpIsOffer;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsOffer);
            _tmpIsOffer = _tmp != 0;
            final boolean _tmpIsNewArrival;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsNewArrival);
            _tmpIsNewArrival = _tmp_1 != 0;
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new ProductEntity(_tmpId,_tmpName,_tmpImageUrl,_tmpShortDescription,_tmpTechnicalSpecsJson,_tmpVariantsJson,_tmpIsOffer,_tmpIsNewArrival,_tmpUpdatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getById(final String id, final Continuation<? super ProductEntity> $completion) {
    final String _sql = "SELECT * FROM products WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ProductEntity>() {
      @Override
      @Nullable
      public ProductEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfImageUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "imageUrl");
          final int _cursorIndexOfShortDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "shortDescription");
          final int _cursorIndexOfTechnicalSpecsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "technicalSpecsJson");
          final int _cursorIndexOfVariantsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "variantsJson");
          final int _cursorIndexOfIsOffer = CursorUtil.getColumnIndexOrThrow(_cursor, "isOffer");
          final int _cursorIndexOfIsNewArrival = CursorUtil.getColumnIndexOrThrow(_cursor, "isNewArrival");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final ProductEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpImageUrl;
            _tmpImageUrl = _cursor.getString(_cursorIndexOfImageUrl);
            final String _tmpShortDescription;
            _tmpShortDescription = _cursor.getString(_cursorIndexOfShortDescription);
            final String _tmpTechnicalSpecsJson;
            _tmpTechnicalSpecsJson = _cursor.getString(_cursorIndexOfTechnicalSpecsJson);
            final String _tmpVariantsJson;
            _tmpVariantsJson = _cursor.getString(_cursorIndexOfVariantsJson);
            final boolean _tmpIsOffer;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsOffer);
            _tmpIsOffer = _tmp != 0;
            final boolean _tmpIsNewArrival;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsNewArrival);
            _tmpIsNewArrival = _tmp_1 != 0;
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new ProductEntity(_tmpId,_tmpName,_tmpImageUrl,_tmpShortDescription,_tmpTechnicalSpecsJson,_tmpVariantsJson,_tmpIsOffer,_tmpIsNewArrival,_tmpUpdatedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
