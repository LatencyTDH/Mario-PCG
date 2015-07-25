from scipy.io.arff import loadarff
from sklearn.svm import SVR
from sklearn.linear_model import LinearRegression
from sklearn.grid_search import GridSearchCV
import numpy as np
import cPickle
import os
import sys
from optparse import OptionParser

# loads a .arff data file
def load_dataset(filename):
    """
    Returns an array of samples X and the class labels y.
    """
    dataset = loadarff(open(filename,'r'))
    features = dataset[1].names()
    class_attr = features[-1]
    y = np.array(dataset[0][class_attr])
    X = np.array(dataset[0][features[:-1]])
    X = np.array(map(lambda x: list(x), X))
    return X,y

def train_SVR_model(X,y):
    """
    Returns the CV-fitted SVM model according to the given training data.
    """
    clf = GridSearchCV(SVR(kernel='rbf', gamma=0.1), cv=5,
                   param_grid={"C": np.logspace(-4,4,9),
                               "gamma": np.logspace(-2, 2, 5)})
    clf.fit(X,y)
    return clf

def train_linear_model(X,y):
    lr = LinearRegression()
    lr.fit(X,y)
    return lr

def save_model(classifier):
    cPickle.dump(classifier, open('svr_model.pkl','wb'))

def load_model():
    """
    Loads the pickled file holding the trained regression model.
    """
    return cPickle.load(open('svr_model.pkl'))

def count_datalines(filename):
    with open(filename,'r') as f:
        text = f.read()
    data_idx = text.index("@data")
    return text[data_idx:].count('\n') - 1

def build_model(filename, toSave=False):
    X,y = load_dataset(filename)
    clf = train_SVR_model(X,y)
    if toSave:
        save_model(clf)
    return clf

def format_feature_vector(fv):
    """
    Transforms the feature vector from a string into a
    correctly formatted list to be used in the regression model.
    """
    return map(float,list(fv.split(',')))

def main():
    clf = None
    filename = "ratings.arff"

    # parse commandline arguments
    op = OptionParser()
    op.add_option("--rebuild",
              action="store_true", dest="rebuild_model",
              help="Rebuilds the regression model with new training data.")
    op.add_option("--fv", 
        action="store", dest="feature_vector", 
        help="Predict regression score from the inputted feature vector")
    op.add_option("--file", 
        action="store", dest="input_file", 
        help="File that contains the user's funness ratings. Defaults to 'ratings.arff'.")
    (opts, args) = op.parse_args()

    if len(args) > 0:
        op.error("Error with argument format!")
        sys.exit(1)

    if opts.input_file:
        filename = opts.input_file
    if opts.rebuild_model:
        print "Rebuilding SV-regression model on new training data..."
        clf = build_model(filename, True)
        print "Done."
    if opts.feature_vector is not None:
        if os.path.isfile('svr_model.pkl'):
            clf = load_model()
        else:
            clf = build_model(filename, True)
        try:
            fv = format_feature_vector(opts.feature_vector)
            print clf.predict(fv)[0]
        except ValueError as e:
            print e.message

if __name__ == "__main__":
    main()
