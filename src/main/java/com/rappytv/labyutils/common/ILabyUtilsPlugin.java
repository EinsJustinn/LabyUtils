package com.rappytv.labyutils.common;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.labymod.serverapi.core.model.display.TabListFlag;
import org.jetbrains.annotations.Nullable;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Logger;

public interface ILabyUtilsPlugin {

    Map<UUID, TabListFlag.TabListFlagCountryCode> cachedFlags = new HashMap<>();
    Gson gson = new Gson();
    HttpClient client = HttpClient.newHttpClient();
    Logger getLogger();

    default String formatNumber(double number) {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        formatter.setMaximumFractionDigits(2);
        formatter.setDecimalFormatSymbols(symbols);

        return formatter.format(number);
    }

    default void getCountryCode(UUID uuid, InetSocketAddress address, Consumer<TabListFlag.@Nullable TabListFlagCountryCode> consumer) {
        if(cachedFlags.containsKey(uuid)) {
            consumer.accept(cachedFlags.get(uuid));
            return;
        }
        if(address == null) {
            consumer.accept(null);
            return;
        }
        String host = address.getHostString();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://api.country.is/" + host))
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();

            client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenAccept(response -> {
                JsonObject body = gson.fromJson(response.body(), JsonObject.class);
                cachedFlags.put(uuid, TabListFlag.TabListFlagCountryCode.valueOf(body.get("country").getAsString()));
                consumer.accept(cachedFlags.get(uuid));
            }).exceptionally(throwable -> {
                getLogger().warning("Failed to get country code of " + host);
                consumer.accept(null);
                return null;
            });
        } catch (Exception e) {
            getLogger().warning("Failed to get country code of " + host);
            consumer.accept(null);
        }
    }
}
