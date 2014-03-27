# EditBox Eclipse plugin

This is converted to git and mavenized EditBox Eclipse plugin created by Piotr Metel.
<http://editbox.sourceforge.net/>

EditBox is using background colors theme to highlight code blocks.

![](http://editbox.sourceforge.net/i/sample-01.png)  

The author was working for project in 2009-2011. Latest version released by Piotr Metel is 0.0.22.
Named as 0.22 Alpha.

Paul Verest has discovered about EditBox though "Added EditBox support #52" <https://github.com/guari/eclipse-ui-theme/pull/52>.
And emailed the author. Whether author will answer or not, having project on GitHub hopefully will revive it.

UPDATE: In one day 2014-03-26 converted to git nad mavenized; added RainbowDrops.es;
 on second day 2014-03-27 changed default theme list to include all themes, add major languages as category,
 make default configuration in [10 lines](https://github.com/Nodeclipse/EditBox/blob/master/pm.eclipse.editbox/src/pm/eclipse/editbox/impl/BoxProviderRegistry.java#L85-95).

While it is nice to play with colors, there are Nodeclipse core projects to do.

## Themes

All themes are inside [pm.eclipse.editbox/src](https://github.com/Nodeclipse/EditBox/tree/master/pm.eclipse.editbox/src)

in `BoxProviderRegistry.java` "Default", "Whitebox", "RainbowDrops", "OnClick", "GreyGradient", "Java_v_20"

- Default is like on image above, differs for java, markup, python, text
- [Whitebox][2] - only border highlight for mouse-over
- [RainbowDrops][4] - for black background themes, e.g. Moonrise, Black by Jeeeyuk, Dark in Eclipse 4.4
- [OnClick][3] - border highlight for mouse-over, and the current block highlight like Default when clicking inside
- GreyGradient
- Java_v_20 

  [2]: https://raw.githubusercontent.com/Nodeclipse/EditBox/master/pm.eclipse.editbox/src/Whitebox.eb
  [3]: https://raw.githubusercontent.com/Nodeclipse/EditBox/master/pm.eclipse.editbox/src/OnClick.eb
  [4]: https://raw.githubusercontent.com/Nodeclipse/EditBox/master/pm.eclipse.editbox/src/RainbowDrops.eb
  
![](https://camo.githubusercontent.com/1baa2b61ed624e6cac336a675737c280d5bddb1a/687474703a2f2f7075752e73682f3742636e442f653131373166633065652e706e67)

It is included in newer version.
For EditBox version <=0.22 download [RainbowDrops.eb inside Moonrise theme](https://github.com/guari/eclipse-ui-theme/blob/master/com.github.eclipseuitheme.themes.plugin/bin/color-scheme/RainbowDrops.eb?raw=true)
 (by right-clicking the link and selecting ```Save link as...```), then import it with Eclipse EditBox Plugin.

Text Category included '*.*' file mask. Remove it for better customization.

## Development

Default themes for a Category are defined in `pm.eclipse.editbox\src\pm\eclipse\editbox\impl\BoxProviderRegistry.java`

In eclipse with PDE (e.g. Enide Studio)
open `plugin.xml` , click `Launch an Eclipse application`.

### Build

`mvn package` to make update site and zip archive.

### Convertion to git notes

Converted with `git svn clone -s http://svn.code.sf.net/p/editbox/code/ editbox3 --trunk=plugin`

<http://stackoverflow.com/questions/9211405/how-to-migrate-code-from-svn-to-git-without-losing-commits-history>

https://sourceforge.net/p/editbox/code/HEAD/tree/plugin/

svn checkout svn://svn.code.sf.net/p/editbox/code/ editbox-code

git svn clone -s svn://svn.code.sf.net/p/editbox/code/


https://sourceforge.net/p/editbox/code/HEAD/tree/

svn checkout http://svn.code.sf.net/p/editbox/code/ editbox-code

git svn clone -s http://svn.code.sf.net/p/editbox/code/
git svn clone -s http://svn.code.sf.net/p/editbox/code/ editbox3 --trunk=plugin
OK
