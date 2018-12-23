## Flyweight模式

flyweight 设计模式主要用于减少创建对象的数量，来减少内存的占用和提高应用的性能。

wiki中有个例子：

flyweight 的一个典型应用是文字处理器，在这个例子中，每个字符都是一个 flyweight 对象，它们共享渲染所需的数据，从而避免了每个字符都需要创建color, font face等等，只需要存储字符的位置即可。
```java
public enum FontEffect {
    BOLD, ITALIC, SUPERSCRIPT, SUBSCRIPT, STRIKETHROUGH
}

public final class FontData {

    /**
     * A weak hash map will drop unused references to FontData.
     * Values have to be wrapped in WeakReferences, 
     * because value objects in weak hash map are held by strong references.
     */
    private static finally WeakHashMap<FontData, WeakReference<FontData>> FLY_WEIGHT_DATA = new WeakHashMap<>();

    private final int pointSize;

    private final String fontFace;

    private final Color color;

    private final Set<FontEffect> effects;

    private FontData(int pointSize, String fontFace, Color color, EnumSet<FontEffect> effects) {
        this.pointSize = pointSize;
        this.fontFace = fontFace;
        this.color = color;
        this.effects = Collections.unmodifiableSet(effects);
    }

    public static FontData create(int pointSize, String fontFace, Color color,
        FontEffect... effects) {

        EnumSet<FontEffect> effectsSet = EnumSet.noneOf(FontEffect.class);

        for (FontEffect fontEffect : effects) {
            effectsSet.add(fontEffect);
        }

        FontData data = new FontData(pointSize, fontFace, color, effectsSet);

        WeakReference<FontData> ref = FLY_WEIGHT_DATA.get(data);

        FontData result = (ref != null) ? ref.get() : null;

        if (result == null) {
            FLY_WEIGHT_DATA.put(data, new WeakReference<FontData>(data));
            result = data;
        }
        
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FontData) {
            if (obj == this) {
                return true;
            }
            FontData other = (FontData) obj;
            return other.pointSize == pointSize && other.fontFace.equals(fontFace)
                && other.color.equals(color) && other.effects.equals(effects);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (pointSize * 37 + effects.hashCode() * 13) * fontFace.hashCode();
    }
}
```
看起来非常像单例模式。

baeldung 的 blog 上强调了：
> It’s very important that the flyweight objects are immutable: any operation on the state must be performed by the factory.

flyweight 对象是不可变的，而 Singleton 经常会变化。

参考：
- https://zh.wikipedia.org/wiki/%E4%BA%AB%E5%85%83%E6%A8%A1%E5%BC%8F
- https://www.baeldung.com/java-flyweight
