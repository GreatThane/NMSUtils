# NMSUtils
As I've grown more comfortable with plugin development, I've gotten more and more infuriated having to work with modules to get mutli-version support, and I really couldn't find any libraries out there that don't limit what you can do with NBT. So, I've created my own library and dare I say the first I could find that actually *adds* functionality to item NBT. As of right now, it only supports 1.13.2 and I do not plan on supporting later versions either (that being said, I will include a "vNext" module that will use spigot's new and shiny included NBT library API just for smooth transitioning for those who choose to use this library other than myself), but over time I will add support for older versions back to probably 1.8 at the most. But before backwards compatibility, I want to make sure I catch all my bases for NBT use, meaning not just items, but blocks and entities as well. But, if you're here as a developer, you probably don't care about that and just want to see examples already.
## Usage
You can get an instance of the NBT object one of three ways: through the `NMSUtils.getNBT(ItemStack)` method, the `NMSUtils.getEmptyNBT()` method, or the `NMSUtils.toNBT(JsonObject)` method. You can also use the constructor for the NBT object, but I strongly discourage that unless you plan to only use the object for storing data and never applying it to an item.
Now, let's say you wanted to add a cost tag to your item. All one has to do is something along these lines:
```java
NBT nbt = NMSUtils.getNBT(stack);
nbt.addNBT("cost", 65);
player.getInventory().addItem(nbt.copyOnto(stack));
```
In this example, I use the `copyOnto` method, which returns a copy of the provided item stack with the nbt applied to it. This is useful if you want to apply the same NBT data to multiple items, or preserve the original item stack's unmodified state. I would recommend using this method whenever possible, but if there is a case where you absolutely must modify the NBT of an item stack instance and you cannot afford creating a new copy, you can use `applyTo(ItemStack)`. However, this method takes advantage of a small amount of reflection and could cost some of your server performance.
But what about getting our information back out of our item's nbt? Why, a simple
```java
int cost = nbt.getNBT("cost", int.class);
```
will do the trick! But lets say you don't want to work with my library any more than you have to. To help with that, I've included a useful method, `nbt.asJsonObject()`, which returns my NBT object as a JsonObject from Google's Gson API. With this, you'll have much more control over the nbt and how you want to organize it.
NBT internally is stored and functions as json, so I based this entirely library on Google's Gson API, and as such it has all the many benefits that come with it, including setting and getting NBT as complex objects instead of having to deal only in primitives.
#### My custom object doesn't work with NBT!
Don't worry, simply use `NMSUtils.getBuilder()` and add your own personal TypeAdapter. After that, call `NMSUtils.instantiateGson()` to get the NBT classes to use your adapter.
#### I've discovered a bug or have thought of a valuable feature!
(Shoot me a message then, why are you checking the readme?...)[https://github.com/GreatThane/NMSUtils/issues]
