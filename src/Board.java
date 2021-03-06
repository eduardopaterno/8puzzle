import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;


    public final class Board {

        private final int[][] tilesCopy;
        private final int N;


        private int hashCode = -1;
        private int zeroRow = -1;
        private int zeroCol = -1;
        private Collection<Board> neighbors;

        public Board(int[][] tiles) {
            this.N = 4; //número da matriz
            this.tilesCopy = new int[N][N]; //cria matriz
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    if (tiles[i][j] >= 0 && tiles[i][j] < N*N) tilesCopy[i][j] = tiles[i][j];//verifica se o número é válido
                    else {
                        System.out.printf("Illegal tile value at (%d, %d): "
                                + "should be between 0 and N^2 - 1.", i, j);
                        System.exit(1);
                    }
                }
            }
            checkRep();
        }

        public int tileAt(int row, int col) {
            if (row < 0 || row > N - 1) throw new IndexOutOfBoundsException //nao deixa sair do tabuleiro
                    ("row should be between 0 and N - 1");
            if (col < 0 || col > N - 1) throw new IndexOutOfBoundsException
                    ("col should be between 0 and N - 1");

            return tilesCopy[row][col]; //retorna posição de um quadrado em "row" "col"
        }

        public int size() { //retorna o tamanho do tabuleiro
            return N;
        }

        public int hamming() { //função de hamming, conta os quadrados fora do lugar correto
            int hamming = 0;
            for (int row = 0; row < this.size(); row++) {
                for (int col = 0; col < this.size(); col++) {
                    if (tileAt(row, col) != 0 && tileAt(row, col) != (row*N + col + 1)) hamming++;
                }
            }
            return hamming;
        }

        public int manhattan() { //soma da distância dos quadrados até o lugar certo
            int manhattan = 0;

            int expectedRow = 0, expectedCol = 0;
            for (int row = 0; row < this.size(); row++) {
                for (int col = 0; col < this.size(); col++) {
                    if (tileAt(row, col) != 0 && tileAt(row, col) != (row*N + col + 1)) {
                        expectedRow = (tileAt(row, col) - 1) / N;
                        expectedCol = (tileAt(row, col) - 1) % N;
                        manhattan += Math.abs(expectedRow - row) + Math.abs(expectedCol - col);
                    }
                }
            }
            return manhattan;
        }

        public boolean isGoal() { //verifica se já está correto

            if (tileAt(N-1, N-1) != 0) return false;

            for (int i = 0; i < this.size(); i++) {
                for (int j = 0; j < this.size(); j++) {
                    if (tileAt(i, j) != 0 && tileAt(i, j) != (i*N + j + 1)) return false;
                }
            }

            return true;
        }
        public boolean isSolvable() { //verifica se é resolvível
            int inversions = 0;

            for (int i = 0; i < this.size() * this.size(); i++) {
                int currentRow = i / this.size();
                int currentCol = i % this.size();

                if (tileAt(currentRow, currentCol) == 0) {
                    this.zeroRow = currentRow;
                    this.zeroCol = currentCol;
                }

                for (int j = i; j < this.size() * this.size(); j++) {
                    int row = j / this.size();
                    int col = j % this.size();


                    if (tileAt(row, col) != 0 && tileAt(row, col) < tileAt(currentRow, currentCol)) {
                        inversions++;
                    }
                }
            }

            if (tilesCopy.length % 2 != 0 && inversions % 2 != 0) return false;
            if (tilesCopy.length % 2 == 0 && (inversions + this.zeroRow) % 2 == 0) return false;

            return true;
        }



        @Override
        public boolean equals(Object y) { //verifica se é igual ao tabuleiro y
            if (!(y instanceof Board)) return false;
            Board that = (Board) y;
            return this.tileAt(N - 1, N - 1) == that.tileAt(N - 1, N - 1) && this.size() == that.size() && Arrays.deepEquals(this.tilesCopy, that.tilesCopy);

        }

        public Collection<Board> neighbors() { //gera os tabuleiros vizinhos
            if (neighbors != null) return neighbors;
            if (this.zeroRow == -1 && this.zeroCol == -1) findZeroTile();

            neighbors = new HashSet<>();

            if (zeroRow - 1 >= 0)           generateNeighbor(zeroRow - 1, true);
            if (zeroCol - 1 >= 0)           generateNeighbor(zeroCol - 1, false);
            if (zeroRow + 1 < this.size())  generateNeighbor(zeroRow + 1, true);
            if (zeroCol + 1 < this.size())  generateNeighbor(zeroCol + 1, false);

            return neighbors;
        }

        private void findZeroTile() {
            outerloop:
            for (int i = 0; i < this.size(); i++) {
                for (int j = 0; j < this.size(); j++) {
                    if (tileAt(i, j) == 0) {
                        this.zeroRow = i;       // indice começa em 0
                        this.zeroCol = j;
                        break outerloop;
                    }
                }
            }
        }
        private void generateNeighbor(int toPosition, boolean isRow) {
            Board board = new Board(this.tilesCopy);
            if (isRow)  swapEntries(board.tilesCopy, zeroRow, zeroCol, toPosition, zeroCol);
            else        swapEntries(board.tilesCopy, zeroRow, zeroCol, zeroRow, toPosition);

            neighbors.add(board);
        }


        private void swapEntries(int[][] array, int fromRow, int fromCol, int toRow, int toCol) {
            int i = array[fromRow][fromCol];
            array[fromRow][fromCol] = array[toRow][toCol];
            array[toRow][toCol] = i;
        }

        public String toString() { //transforma em string
            StringBuilder s = new StringBuilder(4 * N * N);
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    s.append(String.format("%2d ", tileAt(i, j)));
                }
                s.append("\n");
            }
            return s.toString();
        }

        private void checkRep() {
            assert tilesCopy.length > 0;
        }
    }
