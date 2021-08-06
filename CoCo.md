### Model

#### ChangeRecord

对应changes.csv文件中的一个条目，记录每一个file的每一次commit

#### Cochange

extends FilePair

只有一个属性 private ArrayList<Tuple<RevCommit>> coVersions，Tuple中有两个item分别为fuzzy overlap的两个文件的commit，coVersions中包含所有commit对

#### FileChange

一个文件的所有commit：ArrayList<RevCommit> commits

记录文件在历史中最后一次commit时的路径为lastPath

#### FilePair

对应file_pairs.csv中的一个条目，记录所有文件的排列组合？

共有2000多个文件n(n-1)/2

#### GitProject

两种方法initialized project：本地导入；Clone and load

getWalk()从配置的LastCommit遍历所有的RevCommit，跳过merge



### Config

#### ConfigurationManager

private static Properties properties;

private static HashMap<String, String> testProperties = new HashMap<>();

用于自动化测试，会覆盖配置文件

#### SourcesManager

private static Properties properties;

private static List<String> directories;

读取获取sources.properties以分析项目的存放目录



### cochanges

#### ChangeDetector

RENAME_SCORE：以两个文件之间字节相似度百分比判断重命名

RENAME_LIMIT：在重命名中比较的最大文件数，避免大幅降度性能

InitialiseChangeHistory方法获取所有的commit，commit顺序为从最新的开始。

通过getParent获取parent0，

获取parentWalk和childWalk的文件并集

calculateFileChanges方法获取文件的修改，判断每一个entries，是否是配置存放目录中的文件，并对modify和rename的修改进行处理，构造FileChange，存入Map<String, FileChange> changeHistory

initDiff方法得到差异

diffFormatter.getRenameDetector判断重命名，

diffFormatter.scan(parent, child)检测differences，得到List<DiffEntry> entries

#### CochangeDetector

findCoChanges方法用嵌套的两个for循环逐一对比每个文件的ArrayList<RevCommit>：

如果fileChanges1.size()*fileChanges2.size()的数量小于cochange的最小数量阈值，直接跳过

再用relatedChanges判断两个文件交叉的ArrayList<Tuple<RevCommit>>

超过阈值的标记为cochange，添加进coChanges序列中

relatedChanges方法输入两个文件的ArrayList<RevCommit> ，返回交叉的RevCommit对的list，即ArrayList<Tuple<RevCommit>>

> change1和change2的时间为什么是升序排列？

用嵌套的两个for循环逐一对比RevCommit，commit距离和时间距离两个维度同时满足才被判定为cochange

> 会存在两个文件分别连续4次commit，任意两次都在范围内，所以有可能被记录为4*4=16次cochange。或者一个文件commit了1次，另一个文件在这次commit附近疯狂提交，就被判定为cochange了的情况嘛？





效率：

argpmul 2200左右个文件，4000个commit，72s，得到cochange对85个











