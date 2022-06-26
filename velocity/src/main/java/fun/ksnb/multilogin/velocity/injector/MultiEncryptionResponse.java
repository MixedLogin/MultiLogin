package fun.ksnb.multilogin.velocity.injector;

import com.velocitypowered.proxy.connection.MinecraftSessionHandler;
import com.velocitypowered.proxy.connection.client.InitialLoginSessionHandler;
import com.velocitypowered.proxy.protocol.packet.EncryptionResponse;
import lombok.AllArgsConstructor;
import moe.caa.multilogin.api.logger.LoggerProvider;
import moe.caa.multilogin.api.main.MultiCoreAPI;
import net.kyori.adventure.text.Component;

@AllArgsConstructor
public class MultiEncryptionResponse extends EncryptionResponse {
    private final MultiCoreAPI multiCoreAPI;

    @Override
    public boolean handle(MinecraftSessionHandler handler) {
        if (!(handler instanceof InitialLoginSessionHandler)) {
            return super.handle(handler);
        }
        MultiInitialLoginSessionHandler multiInitialLoginSessionHandler = new MultiInitialLoginSessionHandler(((InitialLoginSessionHandler) handler), multiCoreAPI);
        try {
            multiInitialLoginSessionHandler.handle(this);
        } catch (Throwable e) {
            if (multiInitialLoginSessionHandler.isEncrypted()) {
                // TODO: 2022/6/26 tip
                multiInitialLoginSessionHandler.getInbound().disconnect(Component.text(multiCoreAPI.getLanguageHandler().getMessage("velocity_encrypt_error")));
            }
            multiInitialLoginSessionHandler.getMcConnection().close(true);
            LoggerProvider.getLogger().error("An exception occurred while processing the encryption request.", e);
        }
        return true;
    }
}
