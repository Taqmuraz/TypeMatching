import java.util.regex.Pattern;
import java.util.Scanner;
import java.util.regex.Matcher;

interface TextReader
{
    void read(CharSequence text);
}

interface Text
{
    void read(TextReader reader);
}

class CharSequenceText implements Text
{
    CharSequence source;

    public CharSequenceText(CharSequence source) {
        this.source = source;
    }

    @Override
    public void read(TextReader reader)
    {
        reader.read(source);
    }
}

class ConcatCharSequence implements CharSequence
{
    CharSequence a;
    CharSequence b;
    public ConcatCharSequence(CharSequence a, CharSequence b)
    {
        this.a = a;
        this.b = b;
    }
    @Override
    public int length()
    {
        return a.length() + b.length();
    }
    @Override
    public char charAt(int index)
    {
        int al = a.length();
        if(index < al)
        {
            return a.charAt(index);
        }
        return b.charAt(index - al);
    }
    @Override
    public CharSequence subSequence(int start, int end)
    {
        return new SubCharSequence(this, start, end - start);
    }
}

class SplitText implements Text
{
    Text source;
    char separator;

    public SplitText(Text source, char separator)
    {
        this.source = source;
        this.separator = separator;
    }

    @Override
    public void read(TextReader reader)
    {
        source.read(text ->
        {
            int length = text.length();
            int lastSeparator = 0;
            for(int i = 0; i < length; i++)
            {
                char ch = text.charAt(i);
                if(ch == separator)
                {
                    reader.read(new SubCharSequence(text, lastSeparator, i - lastSeparator));
                    lastSeparator = i + 1;
                }
                if(i == length - 1)
                {
                    reader.read(new SubCharSequence(text, lastSeparator, i - lastSeparator + 1));
                }
            }
        });
    }
}

class SubCharSequence implements CharSequence
{
    CharSequence source;
    int start;
    int length;

    SubCharSequence(CharSequence source, int start, int length)
    {
        this.source = source;
        this.start = start;
        this.length = length;
    }

    @Override
    public int length()
    {
        return length;
    }

    @Override
    public char charAt(int index)
    {
        return source.charAt(index + start);
    }

    @Override
    public CharSequence subSequence(int start, int end)
    {
        return new SubCharSequence(this, start, end - start);
    }
}

class EmptyCharSequence implements CharSequence
{
    @Override
    public int length()
    {
        return 0;
    }

    @Override
    public char charAt(int index)
    {
        throw new UnsupportedOperationException("Unimplemented method 'charAt'");
    }

    @Override
    public CharSequence subSequence(int start, int end)
    {
        throw new UnsupportedOperationException("Unimplemented method 'subSequence'");
    }
}

class RemoveSymbolSequence implements CharSequence
{
    CharSequence result;

    RemoveSymbolSequence(CharSequence source, char symbol)
    {
        result = new EmptyCharSequence();
        new SplitText(new CharSequenceText(source), symbol).read(text ->
        {
            result = new ConcatCharSequence(result, text);
        });
    }

    @Override
    public int length()
    {
        return result.length();
    }

    @Override
    public char charAt(int index)
    {
        return result.charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end)
    {
        return new SubCharSequence(result, start, end - start);
    }
}

public class Program
{
    public static void main(String[] args)
    {
        String input = args[0];
        Text separated = new SplitText(new CharSequenceText(new RemoveSymbolSequence(input, ' ')), ',');
        separated.read(text ->
        {
            int length = text.length();
            for(int i = 0; i < length; i++)
            {
                System.out.print(text.charAt(i));
            }
            System.out.print('\n');
        });
    }
}