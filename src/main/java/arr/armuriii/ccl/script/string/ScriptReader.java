package arr.armuriii.ccl.script.string;

import arr.armuriii.ccl.CustomCraftingLibrary;
import arr.armuriii.ccl.resources.script.JsonScript;
import arr.armuriii.ccl.util.StringMathConverter;
import arr.armuriii.ccl.script.json.JsonScriptReader;
import arr.armuriii.ccl.script.json.data.CraftingExtraData;
import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ScriptReader extends AbstractScriptReader{
    private String script;

    public ScriptReader(String script, char returnValue) {
        super(returnValue);
        this.script = removeWhitespace(script);
    }
    public ScriptReader(String script, char returnValue, RecipeInputInventory craftingContainer) {
        super(returnValue,craftingContainer);
        this.script = removeWhitespace(script);
    }
    public ScriptReader(String script, char returnValue, List<ItemStack> stacks) {
        super(returnValue,stacks);
        this.script = removeWhitespace(script);
    }
    public ScriptReader(String script, char returnValue, List<ItemStack> stacks,Map<String,Object> converter) {
        super(returnValue,stacks,converter);
        this.script = removeWhitespace(script);
    }


    public Object replaceExpression() {
        if (this.script.endsWith(".json")) {
            Optional<JsonObject> reader = JsonScript.getScript(new Identifier(this.script.replace(".json","")));
            if (reader.isPresent())
                return JsonScriptReader.evaluate(reader.get(),new CraftingExtraData(this.stacks));
        }
         try {
             skipWhitespace();
             replaceValue();
             return switch (returnValue) {
                 case INT -> (int) StringMathConverter.evaluateExpression(script);
                 case DOUBLE -> StringMathConverter.evaluateExpression(script);
                 case FLOAT -> (float)StringMathConverter.evaluateExpression(script);
                 case BOOLEAN -> StringMathConverter.evaluateExpressionWithBoolean(script) == 1;
                 case LIST, MAP -> returnableObject;
                 case STRING -> appendString(script);
                 case CONSUMER -> script.replace("{","").replace("}","");
                 default -> {
                     if (script.matches("\\{[^}]*}"))
                         yield script.replace("{","").replace("}","");
                     if (script.startsWith("\"") && script.endsWith("\""))
                         yield appendString(script);
                     if (script.matches("[0-9.<>!=&|\\-+/*]+"))
                         yield StringMathConverter.evaluateExpressionWithBoolean(script.replace(" ",""));
                     yield returnableObject;
                 }
             };
         } catch (Exception e) {
             CustomCraftingLibrary.LOGGER.error("Couldn't get return value of Script: {}\n {}", this.script,e.getMessage());
             return "Couldn't get return value of Script: "+ this.script;
         }
     }

    private static String appendString(String string) {
        StringReader reader = new StringReader(string);
        StringBuilder value = new StringBuilder();
        int quote = 0;
        while (reader.canRead()) {
            char c = reader.peek();
            reader.read();
            if ((c == '+' || c == ' ' || c == '(' || c == ')') && quote%2 == 0 && quote!=0)
                continue;
            else if (c == '"' && reader.peek(-1) != '\\') {
                quote++;
                continue;
            }
            value.append(c);
        }
        return value.toString();
    }

    private static String removeWhitespace(String string) {
        StringReader reader = new StringReader(string);
        StringBuilder value = new StringBuilder();
        int quote = 0;
        int apostrophe = 0;
        while (reader.canRead()) {
            char c = reader.peek();
            reader.read();
            if ((c == ' ') && quote%2 == 0 && apostrophe%2 == 0)
                continue;
            else if (c == '"')
                quote++;
            else if (c == '\'')
                apostrophe++;
            value.append(c);
        }
        return value.toString();
    }

    private Object callMethod(Object receiver) {
        readChar();
        skipWhitespace();
        String methodName = readToken();

        if (!methods.containsKey(methodName))
            throw new IllegalArgumentException("Unknown method: " + methodName);
        Method method = methods.get(methodName);
        if (peekChar() == '(') {
            readChar();
            skipWhitespace();
            List<Object> args = parseArgs();
            return method.call(receiver,args);
        }
        return receiver;
    }

    private List<Object> parseArgs() {
        List<Object> args = new ArrayList<>();
        skipWhitespace();

        int start = this.position;
        int ignore = 0;
        while (peekChar() != '\0') {
            skipWhitespace();
            if ((peekChar() == ',' || peekChar() == ')') && ignore==0) {
                if (this.position != start) {
                    ScriptReader subReader = new ScriptReader(this.script.substring(start,Math.min(this.position,this.script.length()))
                            ,Returnable.UNKNOWN.c,stacks,converter);
                    Object arg = subReader.replaceExpression();
                    args.add(arg);
                    start = this.position+1;
                }
            }
            if (peekChar() == '(') {
                ignore++;
                skipWhitespace();
            }
            if (peekChar() == ')') {
                if (ignore == 0)
                    break;
                ignore--;
                skipWhitespace();
            }
            readChar();
        }
        return args;
    }

    private Object callFunction() {
        skipWhitespace();
        int start = this.position;
        if (peekChar() == '\\' || peekChar() == '{' || peekChar() == '"')
            return null;
        if (peekChar(-1) == '\\' || peekChar(-1) == '{' || peekChar(-1) == '"')
            return null;
        String token = readToken();
        if (functions.containsKey(token))
            if (peekChar() == '(') {
                Function function = functions.get(token);
                readChar();
                List<Object> args = parseArgs();
                return function.call(args);
            }

        this.position = start;
        if (this.converter == null)
            return null;
        else
            return this.converter.get(token) instanceof String s ?
                    new ScriptReader(s+";",returnValue.c,stacks,converter).replaceExpression() :
                    this.converter.get(token);
    }

    private void replaceValue() {
        if (script.matches("\\{[^}]*}") && (returnValue == Returnable.CONSUMER || returnValue == Returnable.UNKNOWN))
            return;
        replaceFunctionsByValue();
    }

    private void replaceFunctionsByValue() {
        while (peekChar() != '\0') {
            skipWhitespace();
            int realStart = this.position;
            Object current = callFunction();
            if (current == null) {
                readChar();
                continue;
            }
            StringBuilder replace = new StringBuilder(script.substring(realStart, Math.min(this.position,this.script.length())));
            int start = this.position;
            while (peekChar() != ';' && peekChar() != '\0') {
                skipWhitespace();
                if (peekChar() == '.') {
                    current = callMethod(current);
                }else readChar();
            }
            if (current == null) {
                readChar();
                continue;
            }
            replace.append(script,start,this.position+1);
            if (current instanceof String s)
                if (s.charAt(0)=='"')
                    script = script.replace(replace.toString(),s);
                else
                    script = script.replace(replace.toString(), "\"" + s + "\"");
            else if (current instanceof Number n)
                this.script = this.script.replace(replace.toString(),""+n.doubleValue());
            else
                this.script = this.script.replace(replace.toString(), current.toString());
            this.returnableObject = current;

            readChar();
            this.position = realStart;
        }
        this.position = 0;
    }

    // utils for reading
    private char readChar() {
        if (position < script.length()) {
            return script.charAt(position++);
        }
        return '\0';
    }

    private char peekChar() {
        if (position < script.length()) {
            return script.charAt(position);
        }
        return '\0';
    }

    private char peekChar(@SuppressWarnings("SameParameterValue") int offset) {
        if (position+offset < script.length() && position+offset > 0) {
            return script.charAt(position+offset);
        }
        return '\0';
    }

    private void skipWhitespace() {
        while (position < script.length() && Character.isWhitespace(peekChar())) {
            position++;
        }
    }

    private String readToken() {
        skipWhitespace();
        StringBuilder token = new StringBuilder();
        char current;
        while ((current = peekChar()) != '\0' && current != ' ' && current != '(' && current != ')'
                && current != '+' && current != '-' && current != '*' && current != '/'
                && current != ':' && current != '.') {
            token.append(readChar());
        }
        return token.toString();
    }
}
