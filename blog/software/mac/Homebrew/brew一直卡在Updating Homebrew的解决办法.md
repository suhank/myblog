

# brew一直卡在Updating Homebrew的解决办法

[jouypub](https://yq.aliyun.com/users/vqkv57uji6s6k) 2018-08-02 20:02:00 浏览7439

运行命令brew install pip3，结果界面一直卡在Updating Homebrew...上，有两种解决办法

## 方法一：直接关闭brew每次执行命令时的自动更新（推荐）

```sh
vim ~/.bash_profile

# 新增一行
export HOMEBREW_NO_AUTO_UPDATE=true
```

## 方法二：替换brew源

```sh
cd "$(brew --repo)"
git remote set-url origin https://mirrors.ustc.edu.cn/brew.git

#替换homebrew-core.git
cd "$(brew --repo)/Library/Taps/homebrew/homebrew-core"
git remote set-url origin https://mirrors.ustc.edu.cn/homebrew-core.git
brew update


# 备用地址-1
cd "$(brew --repo)"
git remote set-url origin https://git.coding.net/homebrew/homebrew.git
brew update


# 备用地址-2
cd "$(brew --repo)"
git remote set-url origin https://mirrors.tuna.tsinghua.edu.cn/git/brew.git
cd "$(brew --repo)/Library/Taps/homebrew/homebrew-core"
git remote set-url origin https://mirrors.tuna.tsinghua.edu.cn/git/homebrew-core.git
brew update
```

如果备用地址都不行，那就只能再换回官方地址了

```sh
#重置brew.git
cd "$(brew --repo)"
git remote set-url origin https://github.com/Homebrew/brew.git

#重置homebrew-core.git
cd "$(brew --repo)/Library/Taps/homebrew/homebrew-core"
git remote set-url origin https://github.com/Homebrew/homebrew-core.git
```

 





https://yq.aliyun.com/articles/634494