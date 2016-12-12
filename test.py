#!/usr/bin/python
# -*- coding: utf-8 -*-
from jpype import *

startJVM(getDefaultJVMPath(), "-Djava.class.path=F:\home\hanlp-1.3.1.jar;F:\home\\test.jar;F:\home\\block.jar;F:\home\\gson-2.8.0.jar;F:\home")


Gson = JClass('com.google.gson.Gson')
gson = Gson()

quKey = JClass('QuKeyOld')
instance = quKey()
instance.entry("D:\\zr.txt")

shutdownJVM()
