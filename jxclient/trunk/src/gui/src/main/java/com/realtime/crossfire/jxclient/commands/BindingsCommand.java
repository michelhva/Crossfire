package com.realtime.crossfire.jxclient.commands;

import com.realtime.crossfire.jxclient.gui.keybindings.KeyBinding;
import com.realtime.crossfire.jxclient.gui.keybindings.KeybindingsManager;
import com.realtime.crossfire.jxclient.server.crossfire.CrossfireServerConnection;
import org.jetbrains.annotations.NotNull;

/**
 * Implements the "bindings" command, listing currently active keybindings
 * (for both a character and the global ones).
 * @author Nicolas Weeger
 */
public class BindingsCommand extends AbstractCommand {

  /**
   * Where to find bindings information.
   */
  private final KeybindingsManager keybindingsManager;

  /**
   * Creates a new instance.
   * @param crossfireServerConnection the connection instance
   * @param keybindingsManager bindings information
   */
  public BindingsCommand(@NotNull final CrossfireServerConnection crossfireServerConnection, @NotNull final KeybindingsManager keybindingsManager) {
    super("bindings", crossfireServerConnection);
    this.keybindingsManager = keybindingsManager;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean allArguments() {
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void execute(@NotNull final String args) {
    final Iterable<KeyBinding> bindings = keybindingsManager.getBindingsForPartialCommand("");
    for (final KeyBinding binding : bindings) {
      drawInfo(binding.getBindingDescription() + ": " + binding.getCommandString());
    }
  }
}
