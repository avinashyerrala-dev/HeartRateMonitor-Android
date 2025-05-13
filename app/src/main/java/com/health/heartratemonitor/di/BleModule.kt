package com.health.heartratemonitor.di

//@Module
//@InstallIn(SingletonComponent::class)
object BleModule {
//    @Singleton
//    @Provides
//    fun provideBleDatabase(
//        @ApplicationContext context: Context
//    ): BleDatabase {
//        return Room.databaseBuilder(
//            context,
//            BleDatabase::class.java,
//            "ble_database"
//        ).build()
//    }
//
//    @Provides
//    @Singleton
//    fun provideUserProfileDao(db: BleDatabase): UserProfileDao = db.userProfileDao()
//
//    @Provides
//    @Singleton
//    fun provideActivityDao(db: BleDatabase): ActivityDao = db.activityDao()
//
//    @Singleton
//    @Provides
//    fun provideBleDeviceDao(
//        bleDatabase: BleDatabase
//    ): BleDeviceDao {
//        return bleDatabase.bleDeviceDao()
//    }
//
//    @Singleton
//    @Provides
//    fun provideBleRepository(
//        @ApplicationContext context: Context,
//        bleDeviceDao: BleDeviceDao
//    ): BleRepository {
//        return BleRepositoryImpl(context, bleDeviceDao)
//    }
//
//    @Provides
//    @Singleton
//    fun provideUserProfileRepository(
//        userProfileDao: UserProfileDao
//    ): UserProfileRepository = UserProfileRepositoryImpl(userProfileDao)
//
//    @Provides
//    @Singleton
//    fun provideScanForDevicesUseCase(
//        repository: BleRepository
//    ): ScanForDevicesUseCase {
//        return ScanForDevicesUseCase(repository)
//    }
}
