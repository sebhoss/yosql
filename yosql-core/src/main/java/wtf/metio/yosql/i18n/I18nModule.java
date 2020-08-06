/*
 * This file is part of yosql. It is subject to the license terms in the LICENSE file found in the top-level
 * directory of this distribution and at http://creativecommons.org/publicdomain/zero/1.0/. No part of yosql,
 * including this file, may be copied, modified, propagated, or distributed except according to the terms contained
 * in the LICENSE file.
 */
package wtf.metio.yosql.i18n;

import ch.qos.cal10n.IMessageConveyor;
import ch.qos.cal10n.MessageConveyor;
import dagger.Module;
import dagger.Provides;
import wtf.metio.yosql.model.annotations.Localized;
import wtf.metio.yosql.model.annotations.NonLocalized;

import javax.inject.Singleton;
import java.util.Locale;

/**
 * I18N module
 */
@Module
public class I18nModule {

    @Provides
    @Singleton
    Translator provideTranslator(
            @Localized final IMessageConveyor userMessages,
            @NonLocalized final IMessageConveyor systemMessages) {
        return new DefaultTranslator(userMessages, systemMessages);
    }

    @NonLocalized
    @Provides
    @Singleton
    IMessageConveyor provideNonLocalizedIMessageConveyor(@NonLocalized final Locale systemLocale) {
        return new MessageConveyor(systemLocale);
    }

    @Localized
    @Provides
    @Singleton
    IMessageConveyor provideLocalizedIMessageConveyor(@Localized final Locale userLocale) {
        return new MessageConveyor(userLocale);
    }

}