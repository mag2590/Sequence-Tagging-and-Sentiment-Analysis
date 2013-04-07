import fileinput
import re

def main():
    #Training portion    
    train_dict = dict()    
    train_file = input("Enter training file name: ")
    prev_sent = 0
    
    for line in fileinput.input(train_file):
        if line.find("[") == -1 and len(line) > 2:
            line = line.replace('{}','')
            line = line.replace('<','')
            line = line.replace('>','')            
            
            sentiment_val = line.split()[-1]
            line = re.sub(r'-*\d+', '', line)
            
            train_dict[line] = [sentiment_val,prev_sent]
            prev_sent = sentiment_val
    
    #Testing portion
    test_file = input("Enter testing file name: ")
    
    for line in fileinput.input(test_file):
        if line.find("[") == -1 and len(line) > 2:
            line = line.replace('{}','')
            line = line.replace('<','')
            line = line.replace('>','')
            
            max_enc = 0
            max_prob = 0
            for k in train_dict.keys():
                intr_len = len(set(k.split()).intersection(set(line.split())))        
                if intr_len > max_enc:
                    max_enc = intr_len
                    max_prob = train_dict[k][0]
            
            #print(max_prob,max_enc)
                    
main()