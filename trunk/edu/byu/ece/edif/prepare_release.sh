#!/bin/sh

RELEASEROOT='release/byucc/edif'
RELEASEDIRS='jhdl'
PARSERDIRS='javacc libraries/xilinx'

mkdir -p $RELEASEROOT

JSRCS=`grep -E --regexp=[A-Za-z]+[.]java makefile | sed 's/\\\//g'`

touch ${RELEASEROOT}/makefile
echo -n 'ROOT = ../../
include ${ROOT}/byucc/edif/config.mk
CLASSPATH=${ROOT}

JSRCS = \
	' > ${RELEASEROOT}/makefile

i=0
for JFILE in $JSRCS
do
  i=`expr $i + 1`
done

j=0
for JFILE in $JSRCS
do
  cp $JFILE $RELEASEROOT
  echo -n ${JFILE} >> ${RELEASEROOT}/makefile
  j=`expr $j + 1`
  if [ "$i" -gt "$j" ]; then
      echo -n ' \
        ' >> ${RELEASEROOT}/makefile
  else
      echo '' >> ${RELEASEROOT}/makefile
  fi
done

echo '' >> ${RELEASEROOT}/makefile
echo 'JCLASS = $(JSRCS:%.java=%.class)' >> ${RELEASEROOT}/makefile
echo '' >> ${RELEASEROOT}/makefile
echo '' >> ${RELEASEROOT}/makefile
echo 'include ${ROOT}/byucc/edif/common.mk' >> ${RELEASEROOT}/makefile
echo '' >> ${RELEASEROOT}/makefile
echo -n 'all: jclass ' >> ${RELEASEROOT}/makefile

for DIR in $RELEASEDIRS
do
  echo -n $DIR >> ${RELEASEROOT}/makefile
  echo -n '_dir ' >> ${RELEASEROOT}/makefile
done

for DIR in $PARSERDIRS
do
  echo -n `echo $DIR | sed 's/\//_/g'` >> ${RELEASEROOT}/makefile
  echo -n '_dir ' >> ${RELEASEROOT}/makefile
done
echo '' >> ${RELEASEROOT}/makefile
echo '' >> ${RELEASEROOT}/makefile



echo -n 'edifjar: jclass ' >> ${RELEASEROOT}/makefile
for DIR in $RELEASEDIRS
do
  echo -n $DIR >> ${RELEASEROOT}/makefile
  echo -n '_dir ' >> ${RELEASEROOT}/makefile
done
for DIR in $PARSERDIRS
do
  echo -n `echo $DIR | sed 's/\//_/g'` >> ${RELEASEROOT}/makefile
  echo -n '_dir ' >> ${RELEASEROOT}/makefile
done
echo '' >> ${RELEASEROOT}/makefile
echo -n '	cd ../..;byucc/edif;jar -cvf edif.jar byucc/edif/*.class byucc/edif/*.java byucc/edif/makefile byucc/edif/common.mk byucc/edif/config.mk.in byucc/edif/configure README.txt ' >> ${RELEASEROOT}/makefile
for DIR in $RELEASEDIRS
do
  echo -n 'byucc/edif/' >> ${RELEASEROOT}/makefile
  echo -n $DIR >> ${RELEASEROOT}/makefile
  echo -n '/*.class ' >> ${RELEASEROOT}/makefile

  echo -n 'byucc/edif/' >> ${RELEASEROOT}/makefile
  echo -n $DIR >> ${RELEASEROOT}/makefile
  echo -n '/*.java ' >> ${RELEASEROOT}/makefile

  echo -n 'byucc/edif/' >> ${RELEASEROOT}/makefile
  echo -n $DIR >> ${RELEASEROOT}/makefile
  echo -n '/makefile ' >> ${RELEASEROOT}/makefile

  echo -n ' ' >> ${RELEASEROOT}/makefile
done
for DIR in $PARSERDIRS
do
  echo -n 'byucc/edif/' >> ${RELEASEROOT}/makefile
  echo -n $DIR >> ${RELEASEROOT}/makefile
  echo -n '/*.class ' >> ${RELEASEROOT}/makefile

  echo -n 'byucc/edif/' >> ${RELEASEROOT}/makefile
  echo -n $DIR >> ${RELEASEROOT}/makefile
  echo -n '/*.java ' >> ${RELEASEROOT}/makefile

  echo -n 'byucc/edif/' >> ${RELEASEROOT}/makefile
  echo -n $DIR >> ${RELEASEROOT}/makefile
  echo -n '/*.jj ' >> ${RELEASEROOT}/makefile

  if [ -n "`ls ${DIR}/*.jjt`" ]; then
      echo -n 'byucc/edif/' >> ${RELEASEROOT}/makefile
      echo -n $DIR >> ${RELEASEROOT}/makefile
      echo -n '/*.jjt ' >> ${RELEASEROOT}/makefile
  fi

  if [ -n "`ls ${DIR}/*.vhd`" ]; then
      echo -n 'byucc/edif/' >> ${RELEASEROOT}/makefile
      echo -n $DIR >> ${RELEASEROOT}/makefile
      echo -n '/*.vhd ' >> ${RELEASEROOT}/makefile
  fi

  echo -n 'byucc/edif/' >> ${RELEASEROOT}/makefile
  echo -n $DIR >> ${RELEASEROOT}/makefile
  echo -n '/makefile ' >> ${RELEASEROOT}/makefile

  echo -n ' ' >> ${RELEASEROOT}/makefile
done
echo '' >> ${RELEASEROOT}/makefile
echo '' >> ${RELEASEROOT}/makefile



echo -n 'edifjarlite: jclass ' >> ${RELEASEROOT}/makefile
for DIR in $RELEASEDIRS
do
  echo -n $DIR >> ${RELEASEROOT}/makefile
  echo -n '_dir ' >> ${RELEASEROOT}/makefile
done
for DIR in $PARSERDIRS
do
  echo -n `echo $DIR | sed 's/\//_/g'` >> ${RELEASEROOT}/makefile
  echo -n '_dir ' >> ${RELEASEROOT}/makefile
done
echo '' >> ${RELEASEROOT}/makefile
echo -n '	cd ../..;byucc/edif;jar -cvf ediflite.jar byucc/edif/*.class ' >> ${RELEASEROOT}/makefile
for DIR in $RELEASEDIRS
do
  echo -n 'byucc/edif/' >> ${RELEASEROOT}/makefile
  echo -n $DIR >> ${RELEASEROOT}/makefile
  echo -n '/*.class ' >> ${RELEASEROOT}/makefile
done
for DIR in $PARSERDIRS
do
  echo -n 'byucc/edif/' >> ${RELEASEROOT}/makefile
  echo -n $DIR >> ${RELEASEROOT}/makefile
  echo -n '/*.class ' >> ${RELEASEROOT}/makefile
done
echo '' >> ${RELEASEROOT}/makefile
echo '' >> ${RELEASEROOT}/makefile



for DIR in $RELEASEDIRS
do
  echo -n ${DIR} >> ${RELEASEROOT}/makefile
  echo -n '_dir' >> ${RELEASEROOT}/makefile
  echo ':' >> ${RELEASEROOT}/makefile
  echo -n '	(cd ' >> ${RELEASEROOT}/makefile
  echo -n ${DIR} >> ${RELEASEROOT}/makefile
  echo '; make)' >> ${RELEASEROOT}/makefile
  echo '' >> ${RELEASEROOT}/makefile
done
for DIR in $PARSERDIRS
do
  echo -n `echo $DIR | sed 's/\//_/g'` >> ${RELEASEROOT}/makefile
  echo -n '_dir' >> ${RELEASEROOT}/makefile
  echo ':' >> ${RELEASEROOT}/makefile
  echo -n '	(cd ' >> ${RELEASEROOT}/makefile
  echo -n ${DIR} >> ${RELEASEROOT}/makefile
  echo '; make)' >> ${RELEASEROOT}/makefile
  echo '' >> ${RELEASEROOT}/makefile
done

echo 'deepclean: clean' >> ${RELEASEROOT}/makefile
for DIR in $RELEASEDIRS
do
  echo -n '	(cd ' >> ${RELEASEROOT}/makefile
  echo -n ${DIR} >> ${RELEASEROOT}/makefile
  echo -n '_dir' >> ${RELEASEROOT}/makefile
  echo '; make clean)' >> ${RELEASEROOT}/makefile
done
for DIR in $PARSERDIRS
do
  echo -n '	(cd ' >> ${RELEASEROOT}/makefile
  echo -n `echo $DIR | sed 's/\//_/g'` >> ${RELEASEROOT}/makefile
  echo -n '_dir' >> ${RELEASEROOT}/makefile
  echo '; make clean)' >> ${RELEASEROOT}/makefile
done
  echo '' >> ${RELEASEROOT}/makefile

cp configure.in.release ${RELEASEROOT}/configure.in
cp config.mk.in.release ${RELEASEROOT}/config.mk.in
cp common.mk.release ${RELEASEROOT}/common.mk
cp README.txt.release ${RELEASEROOT}/../../README.txt
cp package.html ${RELEASEROOT}/package.html


for DIR in $RELEASEDIRS
do
  mkdir -p ${RELEASEROOT}/${DIR}
  cd ${DIR}
  JSUBSRCS=`grep -E --regexp=[A-Za-z]+[.]java makefile | sed 's/\\\//g'`
  cd ..
  cp ${DIR}/makefile ${RELEASEROOT}/${DIR}
  cp ${DIR}/package.html ${RELEASEROOT}/${DIR}
  for JSUBFILE in $JSUBSRCS
  do
    cp ${DIR}/${JSUBFILE} ${RELEASEROOT}/${DIR}
  done
done

for DIR in $PARSERDIRS
do
  mkdir -p ${RELEASEROOT}/${DIR}
  JSUBSRCS=`ls ${DIR}/*.java ${DIR}/*.jj ${DIR}/*.jjt ${DIR}/*.vhd`
  cp ${DIR}/makefile ${RELEASEROOT}/${DIR}
  cp ${DIR}/package.html ${RELEASEROOT}/${DIR}
  for JSUBFILE in $JSUBSRCS
  do
    cp ${JSUBFILE} ${RELEASEROOT}/${DIR}
  done
done

