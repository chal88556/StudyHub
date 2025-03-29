data("iris")
head(iris)
summary(iris)
library()
"to find principal component"
mypr<-prcomp(iris[,-5],scale=T)
"to understand use of scale"
plot(iris$Sepal.Length,iris$Sepal.Width)
plot(scale(iris$Sepal.Length),scale(iris$Sepal.Width))
mypr
summary(mypr)
plot(mypr,type="l")
biplot(mypr,scale=0)
"extract pc scores"
str(mypr)
mypr$x
iris2<-cbind(mypr$x[,1:2])
head(iris2)
cor(iris[,-5],iris2[,1:2])
install.packages("pls")
library(pls)
names(iris)
