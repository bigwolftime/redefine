Use ByteBuddy and Instrument API for code redefinition(Java)

1. GPG keys upload
   https://central.sonatype.org/pages/working-with-pgp-signatures.html#installing-gnupg

Server List   
* hkp://keys.gnupg.net
* hkp://pool.sks-keyservers.net


2. mvn deploy occur: gpg: signing failed: Inappropriate ioctl for device

原因是 gpg 在当前终端无法弹出密码输入页面
`export GPG_TTY=$(tty)`
~/.bash_profile

reference: https://my.oschina.net/ujjboy/blog/3023151