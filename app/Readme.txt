功能基类

    httplibrary 为基础功能库，不可以用其他功能，只被其他feature和app引用

    commom 为基础功能库，可以引用httplibrary，不可以用其他库，可以被其他feature和app引用

    floatingwindow功能类，可以引用基础功能类，zhi可被app引用

    库直接的引用不能出现相互引用现象，如果出现此现象需要根据具体业务，把冲突代码重新规划库内容



