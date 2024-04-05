package com.libnexus.boidsimulator.console;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.libnexus.boidsimulator.BoidSimulator;
import com.libnexus.boidsimulator.console.command.DefaultCommandSet;
import com.libnexus.boidsimulator.console.command.parse.ConsoleCommand;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public class Console {
    public static final String consoleChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890-=;,./";
    public final List<ConsoleCommand> commands = new LinkedList<>();
    public final BoidSimulator simulator;
    public final Stack<ConsoleMessage> messages = new Stack<>();
    private final BitmapFont bitmapFont = new BitmapFont();
    private final StringBuilder input = new StringBuilder();
    public boolean visible = false;
    private int cursor = 0;
    private int messageCursor = 0;

    public Console(BoidSimulator simulator) {
        this.simulator = simulator;

        DefaultCommandSet defaultCommandSet = new DefaultCommandSet(this);
        commands.addAll(ConsoleCommand.from(defaultCommandSet));
        defaultCommandSet.help();
        log("");

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                String inputKey = Input.Keys.toString(keycode);

                if (visible) {
                    if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                        if (keycode == Input.Keys.LEFT)
                            cursor = 0;
                        else if (keycode == Input.Keys.RIGHT)
                            cursor = input.length();
                        else if (keycode == Input.Keys.UP)
                            messageCursor = messages.size();
                        else if (keycode == Input.Keys.DOWN)
                            messageCursor = 0;
                        else {
                            switch (inputKey) {
                                case "-":
                                    input.insert(cursor, "_");
                                    break;
                                case "=":
                                    input.insert(cursor, "+");
                                    break;
                                case ";":
                                    input.insert(cursor, ":");
                                    break;
                                case ",":
                                    input.insert(cursor, "<");
                                    break;
                                case ".":
                                    input.insert(cursor, ">");
                                    break;
                                case "/":
                                    input.insert(cursor, "?");
                                    break;
                                default: {
                                    if (consoleChars.contains(inputKey))
                                        input.insert(cursor, inputKey);
                                    else
                                        return super.keyDown(keycode);
                                }
                            }
                            cursor++;
                        }

                    } else {
                        if (keycode == Input.Keys.LEFT && cursor > 0)
                            cursor--;
                        else if (keycode == (Input.Keys.RIGHT) && cursor < input.length())
                            cursor++;
                        else if (keycode == Input.Keys.UP && messageCursor < messages.size())
                            messageCursor++;
                        else if (keycode == Input.Keys.DOWN && messageCursor > 0)
                            messageCursor--;
                        else if (keycode == Input.Keys.ENTER)
                            submitCommand();
                        else if (keycode == Input.Keys.BACKSPACE && cursor > 0)
                            input.deleteCharAt(--cursor);
                        else if (keycode == Input.Keys.BACKSPACE && Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
                            input.delete(0, cursor);
                            cursor = 0;
                        } else if (consoleChars.contains(inputKey)) {
                            input.insert(cursor, inputKey.toLowerCase());
                            cursor++;
                        } else if (keycode == Input.Keys.SPACE) {
                            input.insert(cursor, " ");
                            cursor++;
                        }
                    }
                }
                return super.keyDown(keycode);
            }
        });
    }

    public void submitCommand() {
        String command = input.toString();

        input.delete(0, input.length());
        cursor = 0;

        List<String> commandDigestible = Arrays.stream(command.split(" ")).collect(Collectors.toList());
        commandDigestible.removeIf(String::isEmpty);

        if (commandDigestible.isEmpty()) {
            log("");
            return;
        }

        announce("#", Color.WHITE, command);

        String commandName = commandDigestible.remove(0);

        boolean found = false;
        boolean success = false;

        for (ConsoleCommand consoleCommand : commands) {
            if (!consoleCommand.commandAttribute.name().equals(commandName))
                continue;

            found = true;
            if (consoleCommand.invokeIfAccepts(commandDigestible)) {
                success = true;
                break;
            }
        }

        if (!found) {
            error("no command named ", Color.RED, commandName);
        } else if (!success) {
            error("incorrect usage of ", Color.RED, commandName);
        }
    }

    public void draw(SpriteBatch spriteBatch) {
        if (visible) {
            bitmapFont.setColor(new Color(1, 1, 1, 1));
            bitmapFont.draw(spriteBatch, drawableInput(new StringBuilder(input)), 150, 144);

            Stack<ConsoleMessage> showMessages = new Stack<>();
            showMessages.addAll(messages);

            for (int i = 0; i < messageCursor; i++) {
                showMessages.pop();
            }

            float x = 150;
            int y = 0;
            while (!showMessages.isEmpty()) {
                if ((y * 16) > 280)
                    break;

                ConsoleMessage message = showMessages.pop();

                if (message.prefix != null) {
                    bitmapFont.setColor(message.prefixColour);
                    bitmapFont.draw(spriteBatch, message.prefix, 150, 180 + (y * 16f));
                    bitmapFont.setColor(new Color(1, 1, 1, 1));
                    bitmapFont.draw(spriteBatch, ":", 200, 180 + (y * 16f));
                    x = 210;
                }


                for (ConsoleString consoleString : message.message) {
                    final GlyphLayout layout = new GlyphLayout(bitmapFont, consoleString.string);

                    if (layout.width + x > Gdx.graphics.getWidth() - 125)
                        break;

                    bitmapFont.setColor(consoleString.colour);
                    bitmapFont.draw(spriteBatch, consoleString.string, x, 180 + (y * 16f));

                    x += layout.width;
                }
                x = 150;
                y++;
            }
        }
    }

    public void draw(ShapeRenderer shapeRenderer) {
        if (visible) {
            shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(new Color(0, 0, 0, 0.1f));
            shapeRenderer.rect(125, 125, Gdx.graphics.getWidth() - 250, 25);
            shapeRenderer.rect(125, 160, Gdx.graphics.getWidth() - 250, 300);
            shapeRenderer.set(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(new Color(1, 1, 1, 0.1f));
            shapeRenderer.rect(125, 125, Gdx.graphics.getWidth() - 250, 25);
            shapeRenderer.rect(125, 160, Gdx.graphics.getWidth() - 250, 300);
        }
    }

    public String drawableInput(StringBuilder drawableInput) {
        if (drawableInput.isEmpty())
            return "";

        if (cursor < drawableInput.length() / 2) {
            while (notFitInInput(drawableInput.toString())) {
                drawableInput.deleteCharAt(drawableInput.length() - 1);
            }
        } else {
            while (notFitInInput(drawableInput.toString())) {
                drawableInput.deleteCharAt(0);
            }
        }
        return drawableInput.toString();
    }

    private boolean notFitInInput(String text) {
        return notFit(text, Gdx.graphics.getWidth() - 300, 50);
    }

    private boolean notFit(String text, int w, int h) {
        final GlyphLayout layout = new GlyphLayout(bitmapFont, text);
        return !((layout.width < w) && (layout.height < h));
    }

    public void addMessage(ConsoleMessage message) {
        messages.add(message);
    }

    public void log(Object... message) {
        messages.add(ConsoleString.ping(message));
    }

    public void announce(String prefix, Color colour, Object... message) {
        messages.add(ConsoleString.message(prefix, colour, message));
    }

    public void error(Object... message) {
        messages.add(ConsoleString.message("error", Color.RED, message));
    }
}

