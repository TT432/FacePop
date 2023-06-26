package io.github.tt432.facepop.common;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.tt432.facepop.Facepop;
import io.github.tt432.facepop.common.capability.FaceCapability;
import io.github.tt432.facepop.data.FaceBag;
import io.github.tt432.facepop.data.FaceBagManager;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Collection;
import java.util.List;

/**
 * @author TT432
 */
@Mod.EventBusSubscriber
public class CommandHandler {
    @SubscribeEvent
    public static void onEvent(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        CommandBuildContext buildContext = event.getBuildContext();

        ResourceArgument<FaceBag> faceBagResourceArgument = ResourceArgument.resource(buildContext, FaceBagManager.FACE_BAG_KEY);

        dispatcher.register(Commands.literal(Facepop.MOD_ID)
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("unlock")
                        .then(Commands.argument("player", EntityArgument.players())
                                .then(Commands.argument("facebag",faceBagResourceArgument)
                                        .executes(source -> unlockFaceBag(source, EntityArgument.getPlayers(source, "player")))))
                        .then(Commands.argument("facebag", faceBagResourceArgument)
                                .executes(source -> unlockFaceBag(source, List.of(source.getSource().getPlayerOrException()))))));
    }

    private static int unlockFaceBag(CommandContext<CommandSourceStack> source, Collection<ServerPlayer> players) throws CommandSyntaxException {
        Holder.Reference<FaceBag> facebag = ResourceArgument.getResource(source, "facebag", FaceBagManager.FACE_BAG_KEY);

        players.forEach(serverPlayer -> serverPlayer
                .getCapability(FaceCapability.CAPABILITY)
                .ifPresent(capa -> capa.unlock(facebag.key().location().toString())));

        return 0;
    }

    private CommandHandler() {
        // can't instance...
    }
}
