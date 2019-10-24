package vn.ecpay.ewallet.common.dependencyInjection;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import vn.ecpay.ewallet.ECashApplication;

@Module
@Singleton
public class ApplicationModule {
    private ECashApplication application;

    public ApplicationModule(ECashApplication application) {
        this.application = application;
    }

    @Provides
    @Singleton
    public ECashApplication provideApplication() {
        return application;
    }

}
