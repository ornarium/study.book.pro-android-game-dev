//간단한 라이브러리 시험용 프로그램

/* 네이티브 라이브러리를 사용하기 전에 누락된 심볼들이 없는지 확인해야
 * 한다. 오류없이 빌드 되었지만 심볼 누락이 생길수 있기 때문에, 이
 * 부분이 생각보다 어려울수 있다. 심볼 누락이 있는채로 적재하게 되면
 * 적재단계에서 오류가 발생하므로, 이를 해결하는 간단한 해결책으로
 * 라이브러리의 함수를 호출하는 간단한 프로그램으로 미리 링크해 보면
 * 된다 */

/* make가 아니라 직접 컴파일하려면
 * agcc -c testlib.c
 * ald -o testlib testlib.o -L. -lch02
 */

/*
 * 시험 프로그램을 만들어서 테스트하는 것의 장점이 링크 라이브러리를
 * 사용할때 strace로 디버깅을 해볼수있다는 점이다.
 */

/* 동적 링크로 빌드해서 시험하면, 라이브러리를 못찾는 경우가 있다.
 * 그럴때에는 정적링크를 사용한다. ald에는 -nostdlib플래그가 지정되어
 * 있기 때문에 -static 을 이용한 정적 링크에서는 -nostdlib를 제거 해야
 * CodeSourcery Toolchains에서 제대로 빌드 해볼 수 있다
 *
 * agcc -c main.c
 * ald -static -o a.out main.o
 */
#include <stdio.h>

extern int lib_main(int argc, char **argv);

//void _start(int argc, char **argv)
int main(int argc, char **argv) {
        int i;
        printf("Argc=%d Argv=%p\n", argc, argv);
        for(i = 0; i < argc; ++i)
                printf("Main argv[%d]=%s\n", i, argv[i]);

        printf("Starting Lib main sub\n");
        lib_main(argc, argv);

        exit(0);
}
