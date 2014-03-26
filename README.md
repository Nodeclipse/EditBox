# EditBox Eclipse plugin

This is converted to git and mavenized EditBox Eclipse plugin created by Piotr Metel.
<http://editbox.sourceforge.net/>

EditBox is using background colors theme to highlight code blocks.

![](http://editbox.sourceforge.net/i/sample-01.png)  

The author was working for project in 2009-2011. Latest version released by Piotr Metel is 0.0.22.
Named as 0.22 Alpha.

Paul Verest has discovered about EditBox though "Added EditBox support #52" <https://github.com/guari/eclipse-ui-theme/pull/52>.
And emailed the author. Whether author will answer or not, having project on GitHub hopefully will revive it.

## Themes

![](https://camo.githubusercontent.com/1baa2b61ed624e6cac336a675737c280d5bddb1a/687474703a2f2f7075752e73682f3742636e442f653131373166633065652e706e67)

**Black Eclipse themes support:** Download [RainbowDrops.eb inside Moonrise theme](https://github.com/guari/eclipse-ui-theme/blob/master/com.github.eclipseuitheme.themes.plugin/bin/color-scheme/RainbowDrops.eb?raw=true) (by right-clicking the link and selecting ```Save link as...```), then import it with Eclipse EditBox Plugin.

All themes are inside `pm.eclipse.editbox\src`

## Development

Default themes for a Category are defined in `pm.eclipse.editbox\src\pm\eclipse\editbox\impl\BoxProviderRegistry.java`


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
