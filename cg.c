#include <stdio.h>
#include <math.h>
#define TILE_WIDTH 2
/*matrix multiplication kernels*/
//non shared
__global__ void
MatrixMul( float *Md , float *Nd , float *Pd , const int WIDTH )
{      // calculate thread id
           unsigned int col = TILE_WIDTH*blockIdx.x + threadIdx.x ;
		   unsigned int row = TILE_WIDTH*blockIdx.y + threadIdx.y ;
		   for (int k = 0 ; k<WIDTH ; k++ )  {
                  Pd[row*WIDTH + col]+= Md[row * WIDTH + k ] * Nd[ k * WIDTH + col] ;
            }// shared
__global__ void
MatrixMulSh( float *Md , float *Nd , float *Pd , const int WIDTH )
{  //Taking shared array to break the MAtrix in Tile widht and fatch them in that array per ele      
        __shared__ float Mds [TILE_WIDTH][TILE_WIDTH] ;
         __shared__ float Nds [TILE_WIDTH][TILE_WIDTH] ;
        // calculate thread id
          unsigned int col = TILE_WIDTH*blockIdx.x + threadIdx.x ;
          unsigned int row = TILE_WIDTH*blockIdx.y + threadIdx.y ;
         for (int m = 0 ; m<WIDTH/TILE_WIDTH ; m++ ) // m indicate number of phase
       {  Mds[threadIdx.y][threadIdx.x] =  Md[row*WIDTH + (m*TILE_WIDTH + threadIdx.x)];
        Nds[threadIdx.y][threadIdx.x] =  Nd[ ( m*TILE_WIDTH + threadIdx.y) * WIDTH + col] ;
         __syncthreads() ; // for syncronizeing the threads
           for ( int k = 0; k<TILE_WIDTH ; k++ )
                       Pd[row*WIDTH + col]+= Mds[threadIdx.x][k] * Nds[k][threadIdx.y] ;
         __syncthreads() ; // for syncronizeing the threads  }}
int main () {
   const int WIDTH = 4 ;
   float array1_h[WIDTH][WIDTH] ,array2_h[WIDTH][WIDTH],result_array_h[WIDTH][WIDTH] ,M_result_array_h[WIDTH][WIDTH]  ;
  float *array1_d , *array2_d ,*result_array_d  ,*M_result_array_d ; // device array
  int i , j ;
  int sx,sy,sz , tx,ty,tz;
  //input in host array
  printf("Enter the scaling factors\n");
  scanf("%d%d%d",&sx,&sy,&sz);
  printf("Enter the translation coordinates\n");
  scanf("%d%d%d",tx,&ty,&tz);
  for ( i = 0 ; i<WIDTH ; i++ ) {
     for (j = 0 ; j<WIDTH ; j++ ){
	    if(i!=j) {
        array1_h[i][j] = 0 ;
        array2_h[i][j] = 0;
		}else {
		 array1_h[i][j] = 1 ;
		 array2_h[i][j] = 1;  }}}
  array2_h[0][3] = tx; array2_h[1][3] = ty;  array2_h[2][3] = tz;
  array2_h[0][0] = sx;  array2_h[1][1] = sy; array2_h[2][2] = sz;
  //create device array cudaMalloc ( (void **)&array_name, sizeofmatrixinbytes) ;
 cudaMalloc((void **) &array1_d , WIDTH*WIDTH*sizeof (int) ) ;
 cudaMalloc((void **) &array2_d , WIDTH*WIDTH*sizeof (int) ) ;
  //copy host array to device array; 
cudaMemcpy ( dest , source , WIDTH , directioncudaMemcpy ( array1_d , array1_h , WIDTH*WIDTH*sizeof (int) , cudaMemcpyHostToDevice ) ;
cudaMemcpy ( array2_d , array2_h , WIDTH*WIDTH*sizeof (int) , cudaMemcpyHostToDevice ) ;
  //allocating memory for resultent device array
 cudaMalloc((void **) &result_array_d , WIDTH*WIDTH*sizeof (int) ) ;
 cudaMalloc((void **) &M_result_array_d , WIDTH*WIDTH*sizeof (int) ) ;
  //calling kernel
dim3 dimGrid ( WIDTH/TILE_WIDTH , WIDTH/TILE_WIDTH ,1 ) ;
dim3 dimBlock( TILE_WIDTH, TILE_WIDTH, 1 ) ;
 // Change if 0 to if 1 for running non shared code and make if 0 for shared memory code
#if 0
 MatrixMul <<<dimGrid,dimBlock>>> ( array1_d , array2_d ,M_result_array_d , WIDTH) ;
#endif
#if 1
    MatrixMulSh<<<dimGrid,dimBlock>>> ( array1_d , array2_d ,M_result_array_d , WIDTH) ;
#endif
 // all gpu function blocked till kernel is working and copy back result_array_d to result_array_h
cudaMemcpy(M_result_array_h , M_result_array_d , WIDTH*WIDTH*sizeof(int) ,cudaMemcpyDeviceToHost) ;
   for ( i = 0 ; i<WIDTH ; i++ ){
      for ( j = 0 ; j < WIDTH ; j++ ) {
        printf ("%f   ",M_result_array_h[i][j] ) ; }
 printf ("\n") ;
}}

