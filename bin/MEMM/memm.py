import fileinput
import re
import nltk
from nltk.corpus import stopwords
from nltk.stem.wordnet import WordNetLemmatizer

train_dict = dict()
pred_sent = 0

'''
    Remove junk content that is numbers and stopwords from the string
    @param string1: string from which we want to remove junk
    @param lines1: lines from which we want to ignore words
    @return: Clean string with no junk parts
'''
def remove_junk(string1):
    #Remove Stopwords and unimportant words
    temp_l = string1.split()
    important_words = filter(lambda x: x not in stopwords.words('english'), temp_l)
    return ' '.join(important_words)

'''
    Get bag of senses for a list of words
    @param temp_words1: Words for which we want to find bag of senses
    @return definitions: Definitions of context words also include hypernyms and hyponyms
'''
def get_bag_of_senses(temp_words):
    senses = []
    lmtzr = WordNetLemmatizer()
    temp_words1 = nltk.pos_tag(temp_words.split())
    #Find the synsets
    for t in temp_words1:
        try:
            if 'VB' in t[1]:
                senses.append(lmtzr.lemmatize(t[0],'v'))
            else:
                senses.append(t[0])
        except:
            pass
    return ' '.join(senses)

def map_lines(sentence1):
    global train_dict
    
    sentence1l = set(sentence1.split())
    
    max_cnt = 0
    max_set = set()
    max_key = ''
    for sentence2 in train_dict.keys():
        sentence2l = set(sentence2.split())
        
        if len(sentence1l.intersection(sentence2l)) > max_cnt:
            max_cnt = len(sentence1l.intersection(sentence2l))
            max_set = sentence2l
            max_key = sentence2
    
    if max_cnt > min(len(sentence1l),len(max_set))/2:
        train_dict[sentence1+' '+max_key] = train_dict[max_key]
        del train_dict[max_key]
        return sentence1+' '+max_key
    return ''

def find_max_match_from_dict(str1):
    global train_dict
    max_cnt = 0
    max_key = ''
    str1 = get_bag_of_senses(remove_junk(str1))
    #print(str1)
    for k in train_dict.keys():
        if len(set(k.split()).intersection(set(str1.split()))) > max_cnt:
            max_cnt = len(set(k.split()).intersection(set(str1.split())))
            max_key = k
    #print(max_key)
    return max_key
    
def memm_prob(line,prev_sent,state):
    global train_dict
    global pred_sent
    if len(train_dict)!= 0:
        key = find_max_match_from_dict(line)
        if key == '':
            return 0
        #print(train_dict[key][int(state)])
        max = 0
        max_i = 0
        for i in range(-3,3):
            for j in range(-3,3):
                if train_dict[key][i][j] > max:
                    max = train_dict[key][i][j]
                    max_i = i
        pred_sent = max_i
        return max
    else:
        return 0
    
def main():
    #Training portion    
    global train_dict
    global pred_sent
    
    count = 0    
    train_file = raw_input("Enter training file name: ")
    prev_sent = '-3'
    
    for line in fileinput.input(train_file):
        if line.find("[") == -1 and len(line) > 2:
            if line.find('{') != -1:
                prev_sent = '-3'
            line = line.replace('{}','')
            line = line.replace('<','')
            line = line.replace('>','')            
            
            sentiment_val = line.split()[-1]
            line = re.sub(r'-*\d+', '', line)
            
            line = get_bag_of_senses(remove_junk(line))
            mapped_key = map_lines(line)
            
            if mapped_key == '' or mapped_key == ' ':
                train_dict[line] = [[0 for x in range(-3,3)] for x in range(-3,3)]
                train_dict[line][int(sentiment_val)][int(prev_sent)] += 1
            else:
                train_dict[mapped_key][int(sentiment_val)][int(prev_sent)] += 1
            
            count += 1
            prev_sent = sentiment_val
            
    for t in train_dict.keys():
        for i in range(-3,3):
            for j in range(-3,3):
                train_dict[t][i][j] /= float(count)
    
    #print(train_dict)
    
    #Testing portion
    test_file = raw_input("Enter testing file name: ")
    viterbi = dict()
    backpointer = dict()
    new_review = False
    line_no = 0
    op = open("answer.txt","w")
    for line in fileinput.input(test_file):
        if line.find("[") == -1 and len(line) > 2:
            if line.find('{') != -1:
                prev_sent = '-3'
            line = line.replace('{}','')
            line = line.replace('<','')
            line = line.replace('>','')
            
            if new_review:
                #print(backpointer)
                new_review = False
                viterbi = dict()
                backpointer = dict()
                line_no = 0
                viterbi[(line_no,-2)] = 1
                viterbi[(line_no,-1)] = 1
                viterbi[(line_no,0)] = 1
                viterbi[(line_no,1)] = 1
                viterbi[(line_no,2)] = 1
            else:
                viterbi[(line_no,-2)] = viterbi[(line_no-1,-2)] * memm_prob(line,prev_sent,-2)
                viterbi[(line_no,-1)] = viterbi[(line_no-1,-1)] * memm_prob(line,prev_sent,-1)
                viterbi[(line_no,0)] = viterbi[(line_no-1,0)] * memm_prob(line,prev_sent,0)
                viterbi[(line_no,1)] = viterbi[(line_no-1,1)] * memm_prob(line,prev_sent,1)
                viterbi[(line_no,2)] = viterbi[(line_no-1,2)] * memm_prob(line,prev_sent,2)
                #print(viterbi[(line_no,-2)],viterbi[(line_no,-1)],viterbi[(line_no,0)],viterbi[(line_no,1)],viterbi[(line_no,2)])
                
                backpointer[line_no] = pred_sent #max(viterbi[(line_no,-2)],viterbi[(line_no,-1)],viterbi[(line_no,0)],viterbi[(line_no,1)],viterbi[(line_no,2)])
                print(pred_sent)
                op.write(str(pred_sent)+'\n')
                '''if backpointer[line_no] == viterbi[(line_no,-2)]:
                    backpointer[line_no] = -2
                elif backpointer[line_no] == viterbi[(line_no,-1)]:
                    backpointer[line_no] = -1
                elif backpointer[line_no] == viterbi[(line_no,0)]:
                    backpointer[line_no] = 0
                elif backpointer[line_no] == viterbi[(line_no,1)]:
                    backpointer[line_no] = 1
                elif backpointer[line_no] == viterbi[(line_no,1)]:
                    backpointer[line_no] = 2'''    
    
            line_no += 1
        else:
            new_review = True  
                        
main()