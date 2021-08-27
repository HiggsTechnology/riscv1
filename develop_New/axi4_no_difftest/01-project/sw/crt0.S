
# init registers incl. stack pointer
# set memory region attribute
# set default trap vector
# jump to main

.text
.global	_start

_start:
    li  x1, 0       # ra
    li  x2, 0       # sp
    li  x3, 0       # gp
    li  x4, 0       # tp
    li  x5, 0       # t0
    li  x6, 0       # t1
    li  x7, 0       # t2
    li  x8, 0       # s0
    li  x9, 0       # s1
    li  x10,0       # a0
    li  x11,0       # a1
    li  x12,0       # a2
    li  x13,0       # a3
    li  x14,0       # a4
    li  x15,0       # a5
    li  x16,0       # a6
    li  x17,0       # a7
    li  x18,0       # s2
    li  x19,0       # s3
    li  x20,0       # s4
    li  x21,0       # s5
    li  x22,0       # s6
    li  x23,0       # s7
    li  x24,0       # s8
    li  x25,0       # s9
    li  x26,0       # s10
    li  x27,0       # s11
    li  x28,0       # t3
    li  x29,0       # t4
    li  x30,0       # t5
    li  x31,0       # t6

    ## init stack
    la   sp, _kernel_stack_
    li   x3, 4096   # stack size
    sub  x3, sp, x3 # stack bottom
INIT_KERLE_STACK:
    sw   x0, 0(x3)
    addi x3, x3, 4
    bne  x3, sp, INIT_KERLE_STACK

    la  x3, _trap_vector_
    csrw  mtvec, x3

    ## setup CSR 0x7F9 MFDC (feature disable control)
    #  MFDC [0] pd   - pipeline disable
    #       [3] bpd  - branch prediction disable
    #       [8] cecd - core ecc check disable
    #      [10] did  - dual-issue disable
    #li    a5, 0x008      # bpd
    #li    a5, 0x408      # bpd + did
    #csrs  0x7f9, a5

    ## set memory region attr
    #  region 8 & 9 are device register area
    #  MRAC = 0x000a0000, bit[19:16]='b1010 (set sideeffect bits of region 8&9)
    lui   a5, 0xa0
    csrw  0x7c0, a5
    fence

    ## jump to main
_to_main_:
    jal   main

_dead_loop_:
    j     _dead_loop_


  .align 4  
_trap_vector_:
    addi  sp, sp, -16
    sw    a1, 12(sp)
    sw    a2,  8(sp)
    csrr  a1, mcause
    bgez  a1, _trap_exception_  # if not negative, goto exception handler

_trap_interrupt_:
    #csrw  meicpct, x0           # capture interrupt claim_id and priority level
    #csrr  a2, meihap            # get vector address
    lw    a2,  8(sp)
    lw    a1, 12(sp)
    addi  sp, sp, 16
    mret

_trap_exception_:
    # to be implemented, dead loop for now
    lw    a2,  8(sp)
    lw    a1, 12(sp)
    addi  sp, sp, 16
    j     _dead_loop_



