import random
import json

CLEAR_COLOR = "\u001B[0m"
RED_COLOR = "\u001B[31m"
YELLOW_COLOR = "\u001B[33m"


# all found qualities (including from past games)
qualityTable = json.load(open("qualities.json"))


# scans a line for a triple
def GetTripleLine(board: list, x: int, y: int, dx: int, dy: int) -> int:
    piece1 = board[x + y * 7]
    x += dx
    y += dy
    piece2 = board[x + y * 7]
    x += dx
    y += dy
    piece3 = board[x + y * 7]

    if piece1 != 0 and piece1 == piece2 and piece2 == piece3:
        return piece1
    
    return 0  # no win


# gets the number of 3 way wins of both colors
def GetTriples(board: list) -> int:
    triples = [0, 0]

    # checking horizontal rows
    for y in range(6):
        numCurrent = 1
        current = board[y * 7]
        for x in range(1, 7):
            if board[x + y * 7] == current:
                numCurrent += 1
                if current != 0 and numCurrent == 3:
                    triples[current - 1] += 1
            else:
                current = board[x + y * 7]
                numCurrent = 1
    
    # checking verticle collumns
    for x in range(7):
        numCurrent = 1
        current = board[x]
        for y in range(1, 6):
            if board[x + y * 7] == current:
                numCurrent += 1
                if current != 0 and numCurrent == 3:
                    triples[current - 1] += 1
            else:
                current = board[x + y * 7]
                numCurrent = 1
    
    # checking diagonals
    for x in range(4):
        for y in range(3):
            line = GetTripleLine(board, x, y, 1, 1)
            if line != 0:
                triples[line - 1] += 1

    for x in range(3, 7):
        for y in range(3):
            line = GetTripleLine(board, x, y, -1, 1)
            if line != 0:
                triples[line - 1] += 1

    return triples


# gets the quality of a choice (not based on win-loose-tie)
def GetBasicQuality(board: list, x: int, turn: int) -> float:
    piece = turn + 1
    for y_ in range(1, 6):
        if board[x + y_ * 7] != 0:
            y = y_ - 1
            break
    else:
        y = 5
    
    # getting the number of neighboring pieces of the same color
    numberTouching = 0
    for dx, dy in [[-1, -1], [-1, 0], [-1, 1], [0, -1], [0, 1], [1, -1], [1, 0], [1, 1]]:
        if (x+dx) in range(7) and (y+dy) in range(6):
            if board[x + dx + (y + dy) * 7] == piece:
                numberTouching += 1
    
    quality = numberTouching / 4
    quality += (5 - y) / 8
    quality += (3 - abs(3 - x)) / 6

    newBoard = board[:]
    newBoard[x + y * 7] = turn + 1
    triples = GetTriples(newBoard)

    quality += triples[turn] / 2
    quality -= triples[1 - turn] / 2

    return quality + random.uniform(-0.75, 0.75)


# scans a line for a win
def GetWinLine(board: list, x: int, y: int, dx: int, dy: int) -> int:
    piece1 = board[x + y * 7]
    x += dx
    y += dy
    piece2 = board[x + y * 7]
    x += dx
    y += dy
    piece3 = board[x + y * 7]
    x += dx
    y += dy
    piece4 = board[x + y * 7]

    if piece1 != 0 and piece1 == piece2 and piece2 == piece3 and piece3 == piece4:
        return (piece1 - 1) * 4 - 2  # a win
    
    return 0  # no win

# gets the current win condition
def GetWinPrint(board: list) -> int:
    # checking horizontal rows
    for y in range(6):
        numCurrent = 1
        current = board[y * 7]
        for x in range(1, 7):
            if board[x + y * 7] == current:
                numCurrent += 1
                if current != 0 and numCurrent == 4:
                    print("horizontal win")
                    return (current - 1) * 4 - 2
            else:
                current = board[x + y * 7]
                numCurrent = 1
    
    # checking verticle collumns
    for x in range(7):
        numCurrent = 1
        current = board[x]
        for y in range(1, 6):
            if board[x + y * 7] == current:
                numCurrent += 1
                if current != 0 and numCurrent == 4:
                    print("verticle win")
                    return (current - 1) * 4 - 2
            else:
                current = board[x + y * 7]
                numCurrent = 1
    
    # checking diagonals
    for x in range(4):
        for y in range(3):
            line = GetWinLine(board, x, y, 1, 1)
            if line != 0:
                print("diagonal left win")
                return line

    for x in range(3, 7):
        for y in range(3):
            line = GetWinLine(board, x, y, -1, 1)
            if line != 0:
                print("diagonal right win")
                return line

    # checking for a tie
    for x in range(7):
        for y in range(6):
            if board[x + y * 7] == 0:
                return 0

    print("tie")
    return -1  # tie


# gets the current win condition
def GetWin(board: list) -> int:
    # checking horizontal rows
    for y in range(6):
        numCurrent = 1
        current = board[y * 7]
        for x in range(1, 7):
            if board[x + y * 7] == current:
                numCurrent += 1
                if current != 0 and numCurrent == 4:
                    return (current - 1) * 4 - 2
            else:
                current = board[x + y * 7]
                numCurrent = 1
    
    # checking verticle collumns
    for x in range(7):
        numCurrent = 1
        current = board[x]
        for y in range(1, 6):
            if board[x + y * 7] == current:
                numCurrent += 1
                if current != 0 and numCurrent == 4:
                    return (current - 1) * 4 - 2
            else:
                current = board[x + y * 7]
                numCurrent = 1
    
    # checking diagonals
    for x in range(4):
        for y in range(3):
            line = GetWinLine(board, x, y, 1, 1)
            if line != 0:
                return line

    for x in range(3, 7):
        for y in range(3):
            line = GetWinLine(board, x, y, -1, 1)
            if line != 0:
                return line

    # checking for a tie
    for x in range(7):
        for y in range(6):
            if board[x + y * 7] == 0:
                return 0

    return -1  # tie


# gets a string representation of the board
def GetBoardString(board: list) -> str:
    return str(board)


# getting the quality recursivly
numTableGrabs = 0
def GetQuality(board: list, turn: int, depth: int) -> int:
    global numTableGrabs
    # checking if the position has already been found
    position = GetBoardString(board)
    if position in qualityTable:
        numTableGrabs += 1
        return qualityTable[position]
    
    # stoping the program at a max depth
    win = GetWin(board)
    if win != 0:
        qualityTable[GetBoardString(board)] = win
        return win
    elif depth > 4:
        return 0
    
    # finding the worst option for the player to take (in the eyes of the AI)
    if turn == 0:
        # computing the values of different placement options
        worst = 5
        for x in range(7):
            if board[x] == 0:
                newBoard = board[:]
                for y in range(1, 6):
                    if newBoard[x + y * 7] != 0:
                        newBoard[x + (y - 1) * 7] = turn + 1
                        break
                else:
                    newBoard[x + 35] = turn + 1
                
                value = GetQuality(newBoard, 1 - turn, depth + 1)
                if value != 0:
                    qualityTable[GetBoardString(newBoard)] = value
                if value < worst:
                    worst = value
                    if worst == -2:
                        qualityTable[GetBoardString(board)] = worst
                        return worst
        if worst != 0:
            qualityTable[GetBoardString(board)] = worst
        
        return worst
    
    # finding the best option for the AI to take
    best = -5
    for x in range(7):
        if board[x] == 0:
            newBoard = board[:]
            for y in range(1, 6):
                if newBoard[x + y * 7] != 0:
                    newBoard[x + (y - 1) * 7] = turn + 1
                    break
            else:
                newBoard[x + 35] = turn + 1

            value = GetQuality(newBoard, 1 - turn, depth + 1)
            if value != 0:
                qualityTable[GetBoardString(newBoard)] = value
            if value > best:
                best = value
                if best == 2:
                    qualityTable[GetBoardString(board)] = best
                    return best

    if best != 0:
        qualityTable[GetBoardString(board)] = best
    return best


def main() -> None:
    global numTableGrabs
    # 0 = none, 1 = red, 2 = yellow
    board = []
    for i in range(7 * 6):
        board.append(0)
    
    #board = [0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 1, 1, 2, 0, 0, 0, 1, 2, 2, 1, 0, 0, 0, 2, 1, 1, 2, 1, 0, 0, 2, 1, 1, 1, 2, 2, 0]
    #GetWinPrint(board)
    #return 0
    # 0 = red, 1 = yellow
    
    turn = 0

    # 0 = none, -1 = tie, -2 = win red, 2 = win yellow
    win = 0
    while not win:
        
        numTableGrabs = 0

        # rendering the board
        layer = ""
        for x in range(7):
            layer += f"|{x+1} |"
        print(layer)
        for y in range(6):
            layer = ""
            for x in range(7):
                if board[x + y * 7] == 0:
                    layer += "|  |"
                elif board[x + y * 7] == 1:
                    layer += f"|{RED_COLOR}⬤{CLEAR_COLOR} |"
                else:
                    layer += f"|{YELLOW_COLOR}⬤{CLEAR_COLOR} |"
            print(layer)

        # having the player place their piece
        if turn == 0:
            print("Players Turn")
            
            """
            valid = False
            while not valid:
                try:
                    x = int(input("X >> ")) - 1
                    if x >= 0 and x < 7:
                        if board[x] == 0:
                            valid = True
                            for y in range(1, 6):
                                if board[x + y * 7] != 0:
                                    board[x + (y - 1) * 7] = turn + 1
                                    break
                            else:
                                board[x + 35] = turn + 1
                except ValueError:
                    pass
            """
             # finding the best move to make
            bests = []
            bestQuality = -5
        
            for x in range(7):
                if board[x] == 0:
                    newBoard = board[:]
                    for y in range(1, 6):
                        if newBoard[x + y * 7] != 0:
                            newBoard[x + (y - 1) * 7] = turn + 1
                            break
                    else:
                        newBoard[x + 35] = turn + 1
                    quality = -GetQuality(newBoard, 1 - turn, 0)
                    print(f"X {x} quality: {quality}")
                    if quality > bestQuality:
                        bestQuality = quality
                        bests = [x]
                        if quality == 2:
                            break
                    elif quality == bestQuality:
                        bests.append(x)
            
            # finding the best index (non-win wise) of the best indexes (win wise)
            bestIndexs = []
            bestIndexQuality = -50000

            for index in range(len(bests)):
                quality = GetBasicQuality(board, bests[index], turn)
                print(f"{bests[index]} final quality: {quality}")
                if quality > bestIndexQuality:
                    bestIndexQuality = quality
                    bestIndexs = [index]
                elif quality == bestIndexQuality:
                    bestIndexs.append(index)
            
            index = random.randint(0, len(bestIndexs) - 1)
            for y in range(1, 6):
                if board[bests[bestIndexs[index]] + y * 7] != 0:
                    board[bests[bestIndexs[index]] + (y - 1) * 7] = turn + 1
                    break
            else:
                board[bests[bestIndexs[index]] + 35] = turn + 1  # """
        
        # having the AI place a piece
        else:
            print("AIs turn")

            # finding the best move to make
            bests = []
            bestQuality = -5
        
            for x in range(7):
                if board[x] == 0:
                    newBoard = board[:]
                    for y in range(1, 6):
                        if newBoard[x + y * 7] != 0:
                            newBoard[x + (y - 1) * 7] = turn + 1
                            break
                    else:
                        newBoard[x + 35] = turn + 1
                    quality = GetQuality(newBoard, 1 - turn, 0)
                    print(f"X {x} quality: {quality}")
                    if quality > bestQuality:
                        bestQuality = quality
                        bests = [x]
                        if quality == 2:
                            break
                    elif quality == bestQuality:
                        bests.append(x)
            
            # finding the best index (non-win wise) of the best indexes (win wise)
            bestIndexs = []
            bestIndexQuality = -50000

            for index in range(len(bests)):
                quality = GetBasicQuality(board, bests[index], turn)
                print(f"{bests[index]} final quality: {quality}")
                if quality > bestIndexQuality:
                    bestIndexQuality = quality
                    bestIndexs = [index]
                elif quality == bestIndexQuality:
                    bestIndexs.append(index)
            
            index = random.randint(0, len(bestIndexs) - 1)
            for y in range(1, 6):
                if board[bests[bestIndexs[index]] + y * 7] != 0:
                    board[bests[bestIndexs[index]] + (y - 1) * 7] = turn + 1
                    break
            else:
                board[bests[bestIndexs[index]] + 35] = turn + 1

        # swapping turns
        turn = 1 - turn

        print(numTableGrabs)

        # checking for a win
        win = GetWinPrint(board)

    # rendering the board
    layer = ""
    for x in range(7):
        layer += f"|{x+1} |"
    print(layer)
    for y in range(6):
        layer = ""
        for x in range(7):
            if board[x + y * 7] == 0:
                layer += "|  |"
            elif board[x + y * 7] == 1:
                layer += f"|{RED_COLOR}⬤{CLEAR_COLOR} |"
            else:
                layer += f"|{YELLOW_COLOR}⬤{CLEAR_COLOR} |"
        print(layer)

    print(win)


main()

# saving the quality table
jsonObj = json.dumps(qualityTable, indent=4)
with open("qualities.json", "w") as out:
    out.write(jsonObj)